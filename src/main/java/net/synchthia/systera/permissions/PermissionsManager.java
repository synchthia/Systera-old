package net.synchthia.systera.permissions;

import com.google.protobuf.ProtocolStringList;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.PlayerAPI;
import org.bukkit.Bukkit;
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

    public void applyPermission(Player player) {
        PermissionAttachment attachment = player.addAttachment(plugin);
        attachments.put(player.getUniqueId(), attachment);

        // Assign Message
        plugin.getLogger().log(Level.INFO, player.getName() + " assigned to: " + PlayerAPI.getGroups(player.getUniqueId()));

        // Set Permission
        ProtocolStringList groups = PlayerAPI.getGroups(player.getUniqueId());
        set(player, attachment, "default");
        groups.forEach(group -> set(player, attachment, group));
    }

    public void set(Player player, PermissionAttachment attachment, String groupName) {
        PermissionsAPI.PermsList permsList = PermissionsAPI.getPermissions(groupName);
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
