package net.synchthia.systera.permissions;

import com.google.protobuf.ProtocolStringList;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class PermissionsAPI {
    private final SysteraPlugin plugin;
    private static Map<String, PermsList> permsListMap = new HashMap<>();

    public PermissionsAPI(SysteraPlugin plugin) {
        this.plugin = plugin;

        try {
            fetchGroups().get(5, TimeUnit.SECONDS);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Failed during FetchData", ex);
        }
    }

    public class PermsList {
        public String name;
        public String prefix;
        public ProtocolStringList globalPerms;
        public ProtocolStringList serverPerms;
    }

    public CompletableFuture<SysteraProtos.FetchGroupsResponse> fetchGroups() {
        return plugin.apiClient.fetchGroups(SysteraPlugin.getServerName()).whenComplete((response, throwable) -> {
            permsListMap.clear();
            response.getGroupsList().forEach(groups -> updateGroup(groups));
        });
    }

    public void updateGroup(SysteraProtos.GroupEntry groups) {
        PermsList permsList = new PermsList();

        permsList.name = groups.getGroupName();
        permsList.prefix = groups.getGroupPrefix();
        permsList.globalPerms = groups.getGlobalPermsList();
        permsList.serverPerms = groups.getServerPermsList();

        if (permsListMap.containsKey(groups.getGroupName())) {
            permsListMap.remove(groups.getGroupName());
        }

        permsListMap.put(groups.getGroupName(), permsList);
        plugin.getLogger().log(Level.INFO, "Detected Group: " + groups.getGroupName());
    }

    public PermsList getPermissions(String group) {
        if (permsListMap.isEmpty()) {
            plugin.permissionsAPI.fetchGroups().join();
        }

        if (!permsListMap.containsKey(group)) {
            return null;
        }
        return permsListMap.get(group);
    }

    public String getPrefix(Player player) {
        String prefix = "&7";
        ProtocolStringList groups = plugin.playerAPI.getGroups(player.getUniqueId());
        if (groups.size() != 0) {
            String primaryGroup = groups.get(groups.size() - 1);
            PermsList permsData = getPermissions(primaryGroup);
            if (permsData != null) {
                prefix = permsData.prefix;
            }
        }
        return prefix.replaceAll("&", "§");
    }
}
