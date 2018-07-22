package net.synchthia.systera.stream;

import com.google.protobuf.ProtocolStringList;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class GroupSubs extends JedisPubSub {
    private static final SysteraPlugin plugin = SysteraPlugin.getInstance();

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        SysteraProtos.GroupStream stream = APIClient.groupStreamFromJson(message);
        assert stream != null;
        switch (stream.getType()) {
            case GROUP:
            case PERMISSIONS:
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    plugin.permissionsAPI.updateGroup(stream.getGroupEntry());
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        ProtocolStringList groups = plugin.playerAPI.getGroups(player.getUniqueId());
                        if (groups.contains(stream.getGroupEntry().getGroupName())) {
                            plugin.permissionsManager.applyPermission(player, groups);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        plugin.getLogger().log(Level.INFO, "P Subscribed : " + pattern);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        plugin.getLogger().log(Level.INFO, "P UN Subscribed : " + pattern);
    }
}
