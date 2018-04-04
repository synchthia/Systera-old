package net.synchthia.systera.stream;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.APIClient;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class ReportSubs extends JedisPubSub {
    private static final SysteraPlugin plugin = SysteraPlugin.getInstance();

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        SysteraProtos.ReportEntryStream stream = APIClient.reportEntryStreamFromJson(message);
        assert stream != null;
        switch (stream.getType()) {
            case REPORT:
                SysteraProtos.ReportEntry entry = stream.getEntry();
                String msg = String.format("&9&lReport &7[%s]&8≫ &6&l %s -> %s &e&l Reported: &a&l%s", entry.getServer(), entry.getFrom().getName(), entry.getTo().getName(), entry.getMessage());
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.hasPermission("systera.report.receive")) {
                        player.sendMessage(StringUtil.coloring(msg));
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