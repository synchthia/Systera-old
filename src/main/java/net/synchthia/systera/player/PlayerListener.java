package net.synchthia.systera.player;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.permissions.PermissionsAPI;
import net.synchthia.systera.punishment.PunishManager;
import net.synchthia.systera.util.StringUtil;
import net.synchthia.systera.world.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Set;
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

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getUniqueId());
        Set<OfflinePlayer> whitelistedPlayers = Bukkit.getWhitelistedPlayers();

        if (Bukkit.hasWhitelist() && !whitelistedPlayers.contains(offlinePlayer)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, ChatColor.GOLD + "This server is Currently Maintenance Mode. Please check Homepage.");
            return;
        }

        try {
            List<SysteraProtos.PunishEntry> punishList = plugin.punishAPI.lookupPlayer(event.getUniqueId(), SysteraProtos.PunishLevel.TBAN).get(5, TimeUnit.SECONDS).getEntryList();
            if (punishList.size() != 0) {
                SysteraProtos.PunishEntry punishEntry = punishList.get(punishList.size() - 1);
                String message = StringUtil.someLineToOneLine(PunishManager.punishMessage(punishEntry)).replaceAll("&","§");
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
                return;
            }
            
            plugin.playerAPI.initPlayerProfile(event.getUniqueId(), event.getName(), event.getAddress().getHostAddress(), event.getAddress().getHostName()).get(10, TimeUnit.SECONDS);
            plugin.playerAPI.fetchPlayerProfile(event.getUniqueId(), event.getName()).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            plugin.getLogger().log(Level.SEVERE, "Exception threw executing onPlayerPreLogin", ex);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Currently not Available");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Permission
        SysteraPlugin.getPermissionsManager().applyPermission(player.getUniqueId(), PlayerAPI.getGroups(player.getUniqueId()));

        // Team
        TeamManager.registerTeam(player);

        // TabList
        TabManager tabManager = new TabManager();
        tabManager.setHeaderFooter(player);

        // Vanish
        event.setJoinMessage(null);
        VanishManager.hideFoundVanishPlayer(player);
        if (PlayerAPI.getSetting(player.getUniqueId(), "vanish")) {
            player.sendMessage(ChatColor.YELLOW + "You are now Vanish! be careful action on this server!!");
            VanishManager.applyVanishInServer(player, true);
        } else {
            sendJoinQuitMessage(ChatColor.GRAY + "Join≫ " + player.getName());
        }

        // Current Server
        plugin.apiClient.setPlayerServer(player.getUniqueId(), player.getServer().getServerName()).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }));
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
        plugin.apiClient.quitServer(player.getUniqueId(), player.getServer().getServerName()).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }));

        // Clear LocalProfile
        PlayerAPI.clearLocalProfile(player.getUniqueId());

        // Permissions
        SysteraPlugin.getPermissionsManager().removePlayerAttachments(player);
    }

    public void sendJoinQuitMessage(String message) {
        //TODO: SettingsからプレイヤーのJoinMessageを表示するかどうかを取得して対象ユーザーにメッセージを送信するようにする
        Bukkit.getOnlinePlayers().forEach(players -> players.sendMessage(message));
    }
}
