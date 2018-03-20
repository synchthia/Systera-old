package net.synchthia.systera.player;

import com.google.protobuf.ProtocolStringList;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.punishment.PunishManager;
import net.synchthia.systera.util.StringUtil;
import net.synchthia.systera.world.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.StampedLock;
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
        ReadWriteLock lock = plugin.getReadWriteLock();
        lock.writeLock().lock();
        try {
            List<SysteraProtos.PunishEntry> punishList = plugin.getPunishAPI().lookupPlayer(event.getUniqueId(), SysteraProtos.PunishLevel.TEMPBAN).get(5, TimeUnit.SECONDS).getEntryList();
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
                return;
            }

//            plugin.getPlayerAPI().initPlayerProfile(event.getUniqueId(), event.getName(), event.getAddress().getHostAddress(), event.getAddress().getHostName());
//            plugin.getPlayerAPI().fetchPlayerProfile(event.getUniqueId(), event.getName());

        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            plugin.getLogger().log(Level.SEVERE, "Exception threw executing onPlayerPreLogin", ex);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Currently not Available");
        } finally {
            lock.writeLock().unlock();
        }
    }


    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        try {
            plugin.getPlayerAPI().initPlayerProfile(player.getUniqueId(), player.getName(), event.getAddress().getHostAddress(), event.getAddress().getHostName());
            plugin.getPlayerAPI().fetchPlayerProfile(player.getUniqueId(), player.getName());
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            plugin.getLogger().log(Level.SEVERE, "Exception threw executing onPlayerPreLogin", ex);
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Currently not Available");
        }

        // Permission
        ProtocolStringList groups = plugin.getPlayerAPI().getGroups(player.getUniqueId());
        if (groups == null) {
            plugin.getLogger().log(Level.WARNING, "GROUPS IS NULL");
        }
        plugin.getPermissionsManager().applyPermission(player, groups);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Vanish
        event.setJoinMessage(null);
        VanishManager.hideFoundVanishPlayer(player);
        if (plugin.getPlayerAPI().getSetting(player.getUniqueId(), "vanish")) {
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

        // Team
        TeamManager.registerTeam(player);

        // TabList
        TabManager tabManager = new TabManager();
        tabManager.setHeaderFooter(player);
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
        plugin.getPlayerAPI().clearLocalProfile(player.getUniqueId());

        // Permissions
        plugin.getPermissionsManager().removePlayerAttachments(player);
    }

    private void sendJoinQuitMessage(String message) {
        //TODO: SettingsからプレイヤーのJoinMessageを表示するかどうかを取得して対象ユーザーにメッセージを送信するようにする
        Bukkit.getOnlinePlayers().forEach(players -> players.sendMessage(message));
    }
}
