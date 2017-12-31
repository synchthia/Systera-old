package net.synchthia.systera.stream;

import lombok.SneakyThrows;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class RedisClient {
    private SysteraPlugin plugin = SysteraPlugin.getInstance();

    private static BukkitTask systemTask;
    private static BukkitTask playerTask;
    private static BukkitTask punishTask;


    private static Boolean wantClose = false;
    private String name;
    private String hostname;
    private Integer port;
    private JedisPool pool;
    private Jedis jedis;

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
        systemTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    pool = new JedisPool(hostname, port);
                    jedis = pool.getResource();
                    jedis.connect();

                    // Subscribe
                    jedis.psubscribe(new SystemSubs(), "systera.system.global", "systera.system." + name);
                } catch (Exception ex) {
                    if (wantClose) {
                        plugin.getLogger().log(Level.INFO, taskName + "Disconnecting from Redis...");
                        jedis.unwatch();
                        jedis.disconnect();
                    } else {
                        ex.printStackTrace();

                        plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds...");
                        Thread.sleep(3000L);
                        runSystemTask();
                    }
                }
            }
        });
    }

    private void runPlayerTask() {
        String taskName = "[PLAYER_TASK] ";
        playerTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    pool = new JedisPool(hostname, port);
                    jedis = pool.getResource();
                    jedis.connect();

                    // Subscribe
                    jedis.psubscribe(new PlayerSubs(), "systera.player.global", "systera.player." + name);
                } catch (Exception ex) {
                    if (wantClose) {
                        plugin.getLogger().log(Level.INFO, taskName + "Disconnecting from Redis...");
                        jedis.unwatch();
                        jedis.disconnect();
                    } else {
                        ex.printStackTrace();

                        plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds...");
                        Thread.sleep(3000L);
                        runPlayerTask();
                    }
                }
            }
        });
    }

    private void runPunishTask() {
        String taskName = "[PUNISH_TASK] ";
        punishTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                try {
                    plugin.getLogger().log(Level.INFO, taskName + "Connecting to Redis: " + hostname + ":" + port);
                    pool = new JedisPool(hostname, port);
                    jedis = pool.getResource();
                    jedis.connect();

                    // Subscribe
                    jedis.psubscribe(new PunishSubs(), "systera.punish.global", "systera.punish." + name);
                } catch (Exception ex) {
                    if (wantClose) {
                        plugin.getLogger().log(Level.INFO, taskName + "Disconnecting from Redis...");
                        jedis.unwatch();
                        jedis.disconnect();
                    } else {
                        ex.printStackTrace();

                        plugin.getLogger().log(Level.WARNING, taskName + "Connection Error! Try Reconnecting every 3 seconds...");
                        Thread.sleep(3000L);
                        runPunishTask();
                    }
                }
            }
        });
    }

    public void disconnect() {
        wantClose = true;
        jedis.disconnect();
        pool.destroy();

        systemTask.cancel();
        playerTask.cancel();
        punishTask.cancel();
    }
}
