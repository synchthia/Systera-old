package net.synchthia.systera.permissions;

import com.google.protobuf.ProtocolStringList;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.PlayerAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class PermissionsManager {
    private final SysteraPlugin plugin;
    private HashMap<UUID, PermissionAttachment> attachments = new HashMap<>();

    public PermissionsManager(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    public void applyPermission(Player player, ProtocolStringList groups) {
//        Player player = Bukkit.getPlayer(playerUUID);
//        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
//        Player player = offlinePlayer.getPlayer();
        PermissionAttachment attachment = player.addAttachment(this.plugin);
        attachments.put(player.getUniqueId(), attachment);

        // Set Prefix
        String coloredPrefix = plugin.getPermissionsAPI().getPrefix(player).replaceAll("&", "§");

        player.setPlayerListName(coloredPrefix + player.getName());

        // Assign Message
        plugin.getLogger().log(Level.INFO, player.getName() + " assigned to: " + plugin.getPlayerAPI().getGroups(player.getUniqueId()));

        // Set Permission
        //set(player, attachment, "default");
        groups.forEach(group -> set(player, attachment, group));
    }

    private void set(Player player, PermissionAttachment attachment, String groupName) {
        PermissionsAPI.PermsList permsList = plugin.getPermissionsAPI().getPermissions(groupName);
        if (permsList == null) {
            return;
        }

        if (permsList.globalPerms != null) {
            permsList.globalPerms.forEach(perms -> {
                if (perms.startsWith("-")) {
                    attachment.setPermission(perms.replaceAll("^-", ""), false);
                } else {
                    attachment.setPermission(perms, true);
                }
            });
        }

        if (permsList.serverPerms != null) {
            permsList.serverPerms.forEach(perms -> {
                if (perms.startsWith("-")) {
                    attachment.setPermission(perms.replaceAll("^-", ""), false);
                } else {
                    attachment.setPermission(perms, true);
                }
            });
        }
        player.recalculatePermissions();
    }

    public void removePlayerAttachments(Player player) {
        attachments.remove(player.getUniqueId());
    }

    public void removeAttachments() {
        if (attachments != null) {
            Bukkit.getOnlinePlayers().stream().map((x) -> attachments.get(x.getUniqueId()))
                    .filter((x) -> x != null).forEach((x) -> x.remove());
        }
    }
}
