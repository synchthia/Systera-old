package net.synchthia.systera.player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.synchthia.systera.SysteraBungee;

import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onServerJoin(ServerConnectEvent event) {

        ProxiedPlayer player = event.getPlayer();
        if (!event.getTarget().canAccess(player)) {
            player.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to access this server.")).create());
            event.setCancelled(true);
            return;
        }

        SysteraBungee.plugin.apiClient.initPlayerProfile(event.getPlayer().getUniqueId(), event.getPlayer().getName(), event.getPlayer().getAddress().getAddress().getHostAddress()).whenComplete((response, throwable) -> {
            if (throwable != null) {
                SysteraBungee.plugin.getLogger().log(Level.WARNING, "Failed Init Player Profile: Exception threw", throwable);
                player.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&cCurrently Not Available on Proxy")).create());
                event.setCancelled(true);
                return;
            }
        });
    }
}
