package net.synchthia.systera;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import lombok.Getter;
import lombok.SneakyThrows;
import net.synchthia.systera.command.*;
import net.synchthia.systera.config.ConfigManager;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.i18n.I18nManager;
import net.synchthia.systera.permissions.PermissionsAPI;
import net.synchthia.systera.permissions.PermissionsManager;
import net.synchthia.systera.player.PlayerAPI;
import net.synchthia.systera.player.PlayerListener;
import net.synchthia.systera.punishment.PunishAPI;
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
    private ConfigManager configManager;

    @Getter
    public APIClient apiClient;
    private String apiServerAddress = "192.168.100.53:17300";

    public PlayerAPI playerAPI;
    public PermissionsAPI permissionsAPI;
    public PunishAPI punishAPI;

    @Getter
    private static PermissionsManager permissionsManager;

    @Getter
    private static ProtocolManager protocolManager;

    private CommandsManager<CommandSender> commands;

    @Override
    public void onEnable() {
        try {
            instance = this;

            this.configManager = new ConfigManager(this);
            this.configManager.load();

            permissionsManager = new PermissionsManager(this);

            protocolManager = ProtocolLibrary.getProtocolManager();
            I18n.setI18nManager(new I18nManager(this));

            registerAPI();
            registerStream();
            registerEvents();
            registerCommands();

            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
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
        apiClient.quitStream(Bukkit.getServerName());
        apiClient.shutdown();

        this.configManager.save();
        getLogger().info(this.getName() + "Disabled");
    }

    private void registerAPI() {
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
                playerAPI.fetchPlayerProfile(player.getUniqueId(), player.getName()).get(5, TimeUnit.SECONDS);
            } catch (Exception ex) {
                getLogger().log(Level.WARNING, "Failed FetchPlayerProfile (AllPlayers)", ex);
            }
        });
    }

    public void registerStream() {
        apiClient.actionStream(Bukkit.getServer().getServerName());
        apiClient.punishStream(Bukkit.getServer().getServerName());
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
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
