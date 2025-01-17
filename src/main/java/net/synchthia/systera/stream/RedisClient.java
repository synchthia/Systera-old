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

    private SystemSubs systemSubs;
    private PlayerSubs playerSubs;
    private PunishmentSubs punishmentSubs;
    private GroupSubs groupSubs;

    public RedisClient(String name, String hostname, Integer port) {
        this.name = name;
        this.hostname = hostname;
        this.port = port;
        this.pool = new JedisPool(hostname, port);

        runSystemTask();
        runPlayerTask();
        runPunishmentTask();
        runGroupTask();
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
                    Jedis jedis = pool.getResource();

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
                    Jedis jedis = pool.getResource();

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

    private void runPunishmentTask() {
        String taskName = "[PUNISHMENT_TASK] ";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    punishmentSubs = new PunishmentSubs();

                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    Jedis jedis = pool.getResource();

                    // Subscribe
                    jedis.psubscribe(punishmentSubs, "systera.punishment.global");
                } catch (Exception ex) {
                    plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds... : ", ex);
                    Thread.sleep(3000L);
                    runPunishmentTask();
                }
            }
        });
    }

    private void runGroupTask() {
        String taskName = "[GROUP_TASK] ";

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    groupSubs = new GroupSubs();

                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    Jedis jedis = pool.getResource();

                    // Subscribe
                    jedis.psubscribe(groupSubs, "systera.group.global", "systera.group." + name);
                } catch (Exception ex) {
                    plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds... : ", ex);
                    Thread.sleep(3000L);
                    runGroupTask();
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
        if (punishmentSubs != null) {
            punishmentSubs.punsubscribe();
        }
        if (groupSubs != null) {
            groupSubs.punsubscribe();
        }

        pool.close();
        pool.destroy();
    }
}
