package net.synchthia.systera.stream;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.permissions.PermissionsManager;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class PlayerSubs extends JedisPubSub {
    private static final SysteraPlugin plugin = SysteraPlugin.getInstance();

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        SysteraProtos.PlayerEntryStream stream = APIClient.playerEntryStreamFromJson(message);
        assert stream != null;
        switch (stream.getType()) {
            case GROUPS:
                UUID playerUUID = APIClient.toUUID(stream.getEntry().getPlayerUUID());
                SysteraPlugin.getInstance().playerAPI.setPlayerGroups(playerUUID, stream.getEntry().getGroupsList());
                PermissionsManager permsManager = new PermissionsManager(SysteraPlugin.getInstance());
                SysteraPlugin.getInstance().getLogger().log(Level.INFO, "onNext > Dispatch > GROUPS Hooked.");
                permsManager.applyPermission(playerUUID, stream.getEntry().getGroupsList());
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
