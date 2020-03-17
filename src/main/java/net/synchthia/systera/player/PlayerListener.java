package net.synchthia.systera.player;

import com.google.protobuf.ProtocolStringList;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.punishment.PunishManager;
import net.synchthia.systera.util.StringUtil;
import net.synchthia.systera.world.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class PlayerListener implements Listener {
    private final SysteraPlugin plugin;

    public PlayerListener(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.getStarted()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server is Starting.");
            return;
        }
        try {
            List<SysteraProtos.PunishEntry> punishList = plugin.punishAPI.lookupPlayer(event.getUniqueId(), SysteraProtos.PunishLevel.TEMPBAN).get(5, TimeUnit.SECONDS).getEntryList();
            if (punishList.size() != 0) {
                SysteraProtos.PunishEntry entry = punishList.get(punishList.size() - 1);
                String message = StringUtil.coloring(
                        StringUtil.someLineToOneLine(
                                PunishManager.punishMessage(
                                        entry.getLevel(),
                                        entry.getReason(),
                                        entry.getDate(),
                                        entry.getExpire()
                                )
                        )
                );
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            plugin.getLogger().log(Level.SEVERE, "Exception threw executing onPlayerPreLogin", ex);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Currently not Available: " + ChatColor.GRAY + "[LOOKUP_ERROR]");
        }
    }


    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED) && !event.getResult().equals(PlayerLoginEvent.Result.KICK_WHITELIST)) {
            return;
        }

        try {
            plugin.playerAPI.initPlayerProfile(player.getUniqueId(), player.getName(), event.getAddress().getHostAddress(), event.getAddress().getHostName());
            plugin.playerAPI.fetchPlayerProfile(player.getUniqueId(), player.getName());
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            plugin.getLogger().log(Level.SEVERE, "Exception threw executing onPlayerPreLogin", ex);
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Currently not Available: " + ChatColor.GRAY + "[PROFILE_FETCH_ERROR]");
        }

        // Permission
        ProtocolStringList groups = plugin.playerAPI.getGroups(player.getUniqueId());
        if (groups == null) {
            plugin.getLogger().log(Level.WARNING, "GROUPS IS NULL");
        }
        plugin.permissionsManager.applyPermission(player, groups);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Vanish
        event.setJoinMessage(null);
        VanishManager.hideFoundVanishPlayer(player);
        if (plugin.playerAPI.getSetting(player.getUniqueId(), "vanish")) {
            player.sendMessage(ChatColor.YELLOW + "You are now Vanish! be careful action on this server!!");
            VanishManager.applyVanishInServer(player, true);
        } else {
            sendJoinQuitMessage(ChatColor.GRAY + "Join≫ " + player.getName());
        }

        // Current Server
        plugin.apiClient.setPlayerServer(player.getUniqueId(), SysteraPlugin.getServerName()).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }));

        // Team
        TeamManager.registerTeam(player);

        // TabList
        TabManager tabManager = new TabManager();
        tabManager.setHeaderFooter(player);

        // Whitelist Notification
        if (player.hasPermission("minecraft.command.whitelist")) {
            if (plugin.getServer().hasWhitelist()) {
                player.sendMessage(ChatColor.GREEN + "* Whitelist is currently enabled");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Team
        TeamManager.unregisterTeam(player);

        // Vanish
        event.setQuitMessage(null);
        if (VanishManager.getVanishPlayerInServer().contains(player)) {
            VanishManager.applyVanishInServer(player, false);
        } else {
            sendJoinQuitMessage(ChatColor.GRAY + "Quit≫ " + player.getName());
        }

        // Current Server
        plugin.apiClient.quitServer(player.getUniqueId(), SysteraPlugin.getServerName()).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }));

        // Clear LocalProfile
        plugin.playerAPI.clearLocalProfile(player.getUniqueId());

        // Permissions
        plugin.permissionsManager.removePlayerAttachments(player);
    }

    @EventHandler
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        Optional<Location> loc = plugin.spawnManager.getSpawnLocation(true, player.getWorld());
        loc.ifPresent(event::setSpawnLocation);
    }

    private void sendJoinQuitMessage(String message) {
        //TODO: SettingsからプレイヤーのJoinMessageを表示するかどうかを取得して対象ユーザーにメッセージを送信するようにする
        Bukkit.getOnlinePlayers().forEach(players -> players.sendMessage(message));
    }
}
