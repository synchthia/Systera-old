package net.synchthia.systera.chat;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author Laica-Lunasys
 */
public class ChatListener implements Listener {
    private final SysteraPlugin plugin;

    public ChatListener(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String prefix = plugin.permissionsAPI.getPrefix(event.getPlayer());
        String playerName = event.getPlayer().getDisplayName();

        String leftFormat = "{prefix}§7{player}§a:§r "
                .replace("{prefix}", prefix)
                .replace("{player}", playerName)
                .replaceAll("&", "§");

        // is Enabled?
        String japanizeMsg = "";
        if (plugin.playerAPI.getSetting(event.getPlayer().getUniqueId(), "japanize")) {
            JapanizeManager japanizeManager = new JapanizeManager();
            String converted = japanizeManager.convert(event.getMessage());
            if (converted != null && !converted.isEmpty()) {
                japanizeMsg = "§6 (" + converted + "§6)";
            }
        }

        /*for (Player receivedPlayer : event.getRecipients()) {
            receivedPlayer.sendMessage(fullFormat);
        }*/
        //Bukkit.getConsoleSender().sendMessage(fullFormat);
        event.setFormat(leftFormat + "%2$s" + japanizeMsg);
        //event.setCancelled(true);
    }
}
