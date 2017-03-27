package net.synchthia.systera.player;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
            plugin.playerAPI.initPlayerProfile(event.getUniqueId(), event.getName(), event.getAddress().getHostAddress()).get(10, TimeUnit.SECONDS);
            plugin.playerAPI.fetchPlayerProfile(event.getUniqueId(), event.getName()).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            plugin.getLogger().log(Level.SEVERE, "Exception threw executing onPlayerPreLogin", ex);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Currently not Available");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.apiClient.setPlayerServer(player.getUniqueId(), player.getServer().getServerName()).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.apiClient.quitServer(player.getUniqueId(), player.getServer().getServerName()).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }));

        plugin.playerAPI.clearPlayerProfile(event.getPlayer().getUniqueId());
    }
}
