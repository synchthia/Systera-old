package net.synchthia.systera;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import lombok.Getter;
import lombok.SneakyThrows;
import net.synchthia.systera.command.AnnounceCommand;
import net.synchthia.systera.command.DispatchCommand;
import net.synchthia.systera.command.SpawnCommand;
import net.synchthia.systera.command.SysteraCommand;
import net.synchthia.systera.config.ConfigManager;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.i18n.I18nManager;
import net.synchthia.systera.player.PlayerAPI;
import net.synchthia.systera.player.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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

    @Getter
    private static ProtocolManager protocolManager;

    private CommandsManager<CommandSender> commands;

    @Override
    public void onEnable() {
        try {
            instance = this;

            this.configManager = new ConfigManager(this);
            this.configManager.load();

            protocolManager = ProtocolLibrary.getProtocolManager();
            I18n.setI18nManager(new I18nManager(this));

            registerAPI();
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
        apiClient.shutdown();
        this.configManager.save();
        getLogger().info(this.getName() + "Disabled");
    }

    private void registerAPI() {
        apiClient = new APIClient(apiServerAddress);
        apiClient.actionStream(Bukkit.getServer().getName());

        playerAPI = new PlayerAPI(this);
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
