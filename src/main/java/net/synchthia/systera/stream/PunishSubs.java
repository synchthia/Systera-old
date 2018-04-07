package net.synchthia.systera.stream;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.punishment.PunishManager;
import redis.clients.jedis.JedisPubSub;

import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class PunishSubs extends JedisPubSub {
    private static final SysteraPlugin plugin = SysteraPlugin.getInstance();

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        SysteraProtos.PunishEntryStream stream = APIClient.punishEntryStreamFromJson(message);
        assert stream != null;
        switch (stream.getType()) {
            case PUNISH:
                SysteraProtos.PunishEntry entry = stream.getEntry();
                PunishManager punishManager = new PunishManager(SysteraPlugin.getInstance());
                punishManager.action(
                        APIClient.toUUID(entry.getPunishedTo().getUUID()),
                        entry.getLevel(),
                        entry.getReason(),
                        entry.getExpire()
                );
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
