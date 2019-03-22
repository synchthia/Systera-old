package net.synchthia.systera;

import co.aikar.commands.BukkitCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.SneakyThrows;
import net.synchthia.systera.chair.ChairListener;
import net.synchthia.systera.chat.ChatListener;
import net.synchthia.systera.command.*;
import net.synchthia.systera.gate.GateListener;
import net.synchthia.systera.gate.GateManager;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.i18n.I18nManager;
import net.synchthia.systera.jumppad.JumpPadListener;
import net.synchthia.systera.location.SpawnManager;
import net.synchthia.systera.permissions.PermissionsAPI;
import net.synchthia.systera.permissions.PermissionsManager;
import net.synchthia.systera.player.PlayerAPI;
import net.synchthia.systera.player.PlayerListener;
import net.synchthia.systera.punishment.PunishAPI;
import net.synchthia.systera.stream.RedisClient;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class SysteraPlugin extends JavaPlugin {
    @Getter
    private static SysteraPlugin instance;

    @Getter
    private Boolean started;

    public BukkitCommandManager cmdManager;

    private static RedisClient redisClient;

    // Server Settings ==================
    @Getter
    private final static String serverId = System.getenv("SERVER_ID") != null ? System.getenv("SERVER_ID") : "unknown";

    @Getter
    private final static String serverName = System.getenv("SERVER_NAME") != null ? System.getenv("SERVER_NAME") : "Unknown";
    // ==================================

    // API ===========================================
    public APIClient apiClient;
    private String apiServerAddress;

    public PlayerAPI playerAPI;
    public PermissionsAPI permissionsAPI;
    public PermissionsManager permissionsManager;

    public PunishAPI punishAPI;

    @Getter
    private static ProtocolManager protocolManager;
    // ================================================

    // Modules ========================================
    public GateManager gateManager;
    public SpawnManager spawnManager;
    // ================================================

    @Override
    public void onEnable() {
        started = false;
        try {
            instance = this;

            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }

            spawnManager = new SpawnManager(this);
            gateManager = new GateManager(this);

            registerRedis();

            registerAPI();
            registerEvents();
            registerCommands();

            permissionsManager = new PermissionsManager(this);

            protocolManager = ProtocolLibrary.getProtocolManager();
            I18n.setI18nManager(new I18nManager(this));

            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            started = true;
            getLogger().info(this.getName() + "Enabled");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Exception threw while onEnable.", e);
            setEnabled(false);
            instance = null;
        }
    }

    @Override
    @SneakyThrows
    public void onDisable() {
        started = false;
        apiClient.shutdown();
        redisClient.disconnect();

        gateManager.save();
        spawnManager.save();
        getLogger().info(this.getName() + "Disabled");
    }

    private void registerRedis() throws InterruptedException {
        String hostname = "localhost";
        Integer port = 6379;

        String redisAddress = System.getenv("SYSTERA_REDIS_ADDRESS");
        if (redisAddress != null) {
            if (redisAddress.contains(":")) {
                String[] splited = redisAddress.split(":");
                hostname = splited[0];
                port = Integer.valueOf(splited[1]);
            } else {
                hostname = redisAddress;
            }
        }

        getLogger().log(Level.INFO, "Redis Address: " + redisAddress);
        redisClient = new RedisClient(SysteraPlugin.getServerId(), hostname, port);
    }

    private void registerAPI() {
        String addressEnv = System.getenv("SYSTERA_API_ADDRESS");
        if (addressEnv == null) {
            apiServerAddress = "localhost:17300";
        } else {
            apiServerAddress = addressEnv;
        }
        getLogger().log(Level.INFO, "API Address: " + apiServerAddress);

        apiClient = new APIClient(apiServerAddress);

        playerAPI = new PlayerAPI(this);
        permissionsAPI = new PermissionsAPI(this);
        punishAPI = new PunishAPI(this);

        Bukkit.getOnlinePlayers().forEach(player -> {
            try {
                playerAPI.fetchPlayerProfileAsync(player.getUniqueId(), player.getName()).get(5, TimeUnit.SECONDS);
            } catch (Exception ex) {
                getLogger().log(Level.WARNING, "Failed FetchPlayerProfile (AllPlayers)", ex);
            }
        });
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(new GateListener(this), this);
        pm.registerEvents(new ChairListener(this), this);

        if (System.getenv("SYSTERA_JUMPPAD_ENABLE") != null &&
                System.getenv("SYSTERA_JUMPPAD_ENABLE").equals("true")) {
            pm.registerEvents(new JumpPadListener(this), this);
        }
    }

    private void registerCommands() {
        cmdManager = new BukkitCommandManager(this);

        cmdManager.getCommandCompletions().registerCompletion("punish_reason", c -> {
            return ImmutableList.of(
                    "Chat Spam (チャットスパム)",
                    "Advertise (広告)",
                    "Glitch (バグや不具合の意図的な不正利用)",
                    "Obscenity / NSFW Content (不適切なコンテンツ) ",
                    "Griefing (他のユーザーへの迷惑行為)",
                    "Violent Language (不適切な発言)",
                    "Watch your language (不適切な発言)",
                    "Hacking - Fly",
                    "Hacking - Nuke",
                    "Hacking - FastRun",
                    "Hacking - FastEat"
            );
        });

        cmdManager.getCommandCompletions().registerAsyncCompletion("player_settings", c -> {
            CommandSender sender = c.getSender();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                return ImmutableList.copyOf(playerAPI.getLocalProfile(player.getUniqueId()).settings.keySet());
            }
            return ImmutableList.of();
        });

        cmdManager.registerCommand(new SysteraCommand(this));
        cmdManager.registerCommand(new DispatchCommand(this));
        cmdManager.registerCommand(new RunasCommand(this));
        cmdManager.registerCommand(new AnnounceCommand(this));
        cmdManager.registerCommand(new SpawnCommand(this));
        cmdManager.registerCommand(new APICommand(this));
        cmdManager.registerCommand(new SettingsCommand(this));
        cmdManager.registerCommand(new PunishCommand(this));
        cmdManager.registerCommand(new ReportCommand(this));
        cmdManager.registerCommand(new ListCommand(this));
        cmdManager.registerCommand(new TellCommand(this));
    }
}
