package net.synchthia.systera.permissions;

import com.google.protobuf.ProtocolStringList;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.PlayerAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class PermissionsAPI {
    private final SysteraPlugin plugin;
    private static Map<String, PermsList> permsListMap = new HashMap<>();

    public PermissionsAPI(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    public class PermsList {
        public String prefix;
        public ProtocolStringList globalPerms;
        public ProtocolStringList serverPerms;
    }

    public CompletableFuture<SysteraProtos.FetchGroupsResponse> fetchGroups() {
        return plugin.apiClient.fetchGroups(Bukkit.getServer().getServerName()).whenComplete((response, throwable) -> {
            permsListMap.clear();

            response.getGroupsList().forEach(groups -> {
                PermsList permsList = new PermsList();

                permsList.prefix = groups.getGroupPrefix();
                permsList.globalPerms = groups.getGlobalPermsList();
                permsList.serverPerms = groups.getServerPermsList();
                permsListMap.put(groups.getGroupName(), permsList);
                plugin.getLogger().log(Level.INFO, "Detected Group: " + groups.getGroupName());
            });
        });
    }

    public static PermsList getPermissions(String group) {
        if (permsListMap.size() == 0) {
            SysteraPlugin.getInstance().permissionsAPI.fetchGroups().join();
        }

        if (!permsListMap.containsKey(group)) {
            return null;
        }
        return permsListMap.get(group);
    }

    public static String getPrefix(Player player) {
        String prefix = "&7";

        Integer groupLength = PlayerAPI.getGroups(player.getUniqueId()).size();
        if (groupLength != 0) {
            String primaryGroup = PlayerAPI.getGroups(player.getUniqueId()).get(groupLength - 1);
            prefix = permsListMap.get(primaryGroup).prefix;
        }

        return prefix.replaceAll("&", "§");
    }
}