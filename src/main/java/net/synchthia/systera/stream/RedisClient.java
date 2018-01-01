package net.synchthia.systera.stream;

import lombok.SneakyThrows;
import net.synchthia.systera.SysteraPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class RedisClient {
    private JedisPool pool;
    private SysteraPlugin plugin = SysteraPlugin.getInstance();
    private String name;
    private String hostname;
    private Integer port;
    private Jedis jedis;

    private SystemSubs systemSubs;
    private PlayerSubs playerSubs;
    private PunishSubs punishSubs;

    public RedisClient(String name, String hostname, Integer port) {
        this.name = name;
        this.hostname = hostname;
        this.port = port;
        runSystemTask();
        runPlayerTask();
        runPunishTask();
    }

    private void runSystemTask() {
        String taskName = "[SYSTEM_TASK] ";
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    systemSubs = new SystemSubs();

                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    pool = new JedisPool(hostname, port);
                    jedis = pool.getResource();
                    jedis.connect();

                    // Subscribe
                    jedis.psubscribe(systemSubs, "systera.system.global", "systera.system." + name);
                } catch (Exception ex) {
                    plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds... : ", ex);
                    Thread.sleep(3000L);
                    runSystemTask();
                }
            }
        });
    }

    private void runPlayerTask() {
        String taskName = "[PLAYER_TASK] ";
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    playerSubs = new PlayerSubs();

                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    pool = new JedisPool(hostname, port);
                    jedis = pool.getResource();
                    jedis.connect();

                    // Subscribe
                    jedis.psubscribe(playerSubs, "systera.player.global", "systera.player." + name);
                } catch (Exception ex) {
                    plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds... : ", ex);
                    Thread.sleep(3000L);
                    runPlayerTask();
                }
            }
        });
    }

    private void runPunishTask() {
        String taskName = "[PUNISH_TASK] ";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    punishSubs = new PunishSubs();

                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    pool = new JedisPool(hostname, port);
                    jedis = pool.getResource();
                    jedis.connect();

                    // Subscribe
                    jedis.psubscribe(punishSubs, "systera.punish.global", "systera.punish." + name);
                } catch (Exception ex) {
                    plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds... : ", ex);
                    Thread.sleep(3000L);
                    runPunishTask();
                }
            }
        });
    }

    public void disconnect() {
        if (systemSubs != null) {
            systemSubs.punsubscribe();
        }
        if (playerSubs != null) {
            playerSubs.punsubscribe();
        }
        if (punishSubs != null) {
            punishSubs.punsubscribe();
        }

        pool.close();
        pool.destroy();
    }
}
