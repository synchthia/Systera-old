package net.synchthia.systera;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
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
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
    private static RedisClient redisClient;

    private CommandsManager<CommandSender> commands;

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
        redisClient = new RedisClient(Bukkit.getServer().getServerName(), hostname, port);
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

        try {
            permissionsAPI.fetchGroups().get(5, TimeUnit.SECONDS);
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, "Failed during FetchData", ex);
        }

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
        this.commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender instanceof ConsoleCommandSender || sender.hasPermission(perm);
            }
        };
        CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, this.commands);
        cmdRegister.register(SysteraCommand.class);
        cmdRegister.register(DispatchCommand.class);
        cmdRegister.register(AnnounceCommand.class);
        cmdRegister.register(SpawnCommand.class);
        cmdRegister.register(APICommand.class);
        cmdRegister.register(SettingsCommand.class);
        cmdRegister.register(PunishCommand.class);
        cmdRegister.register(ReportCommand.class);
        cmdRegister.register(ListCommand.class);
        cmdRegister.register(TellCommand.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender, this);
        } catch (CommandPermissionsException e) {
            I18n.sendMessage(sender, "error.permission");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + "Usage: " + e.getUsage().replace("{cmd}", cmd.getName()));
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + "Usage: " + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                I18n.sendMessage(sender, "error.invalid_option");
            } else {
                sender.sendMessage(ChatColor.RED + "Unknown Error Occurred.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }
}
