package net.synchthia.systera.stream;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.punishment.PunishManager;
import org.bukkit.Bukkit;
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
                String[] messages = PunishManager.punishMessage(entry);
                PunishManager punishManager = new PunishManager(SysteraPlugin.getInstance());
                punishManager.action(Bukkit.getPlayer(entry.getPunishedTo().getName()), entry.getLevel(), messages);
                punishManager.broadcast(entry.getLevel().toString(), entry.getPunishedTo().getName(), entry.getReason());
                break;
        }
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        plugin.getLogger().log(Level.INFO, "P Subscribed : " + pattern);
    }

}
