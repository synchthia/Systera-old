package net.synchthia.systera.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.protobuf.ProtocolStringList;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.permissions.PermissionsManager;
import net.synchthia.systera.player.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

/**
 * @author Laica-Lunasys
 */
@CommandAlias("api")
@CommandPermission("systera.command.api")
@RequiredArgsConstructor
public class APICommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @Subcommand("settings")
    public void onSettings(CommandSender sender, String playerName) {
        sender.sendMessage("Get>>" + playerName);
        Boolean s = plugin.playerAPI.getLocalProfile(Bukkit.getPlayer(sender.getName()).getUniqueId()).settings.get(playerName);
        sender.sendMessage(String.format("%s: %s", playerName, s));
    }

    @Subcommand("shutdown")
    public void onShutdown(CommandSender sender) {
        sender.sendMessage("Shutdown...");
        try {
            plugin.apiClient.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Subcommand("localprofile")
    public void onLocalProfile(CommandSender sender) {
        sender.sendMessage("Profiles: " + plugin.playerAPI.localProfileSize());
    }

    @Subcommand("clearlocalprofile")
    public void onClearLocalProfile(CommandSender sender) {
        sender.sendMessage("Clear: " + sender.getName());
        plugin.playerAPI.clearLocalProfile(Bukkit.getPlayer(sender.getName()).getUniqueId());
    }

    @Subcommand("debugprofile")
    public void onDebugProfile(CommandSender sender) {
        plugin.playerAPI.debug(Bukkit.getPlayer(sender.getName()).getUniqueId());
    }

    @Subcommand("vanish")
    public void onVanish(CommandSender sender) {
        int size = VanishManager.getVanishPlayerInServer().size();
        sender.sendMessage("Vanishing: " + size);
    }

    @Subcommand("groups")
    public void onGroups(CommandSender sender) {
        ProtocolStringList groups = plugin.playerAPI.getLocalProfile(Bukkit.getPlayer(sender.getName()).getUniqueId()).groups;
        sender.sendMessage("You are assigned of: " + groups);
    }

    @Subcommand("fetchperms")
    public void onFetchPerms(CommandSender sender) {
        sender.sendMessage("FetchPerms...");
        SysteraPlugin.getInstance().permissionsAPI.fetchGroups().whenComplete((r, t) -> {
            if (t != null) {
                System.out.println("Error! @ fetch perms api cmd");
                return;
            }
        });
    }

    @Subcommand("updateperms")
    public void onUpdatePerms(CommandSender sender) {
        sender.sendMessage("Apply Permission everyone...");
        PermissionsManager manager = plugin.permissionsManager;
        //manager.paStats(Bukkit.getPlayer(sender.getName()));
        manager.removeAttachments();

        //Bukkit.getOnlinePlayers().forEach((player -> manager.applyPermission(player, plugin.playerAPI.getGroups(player.getUniqueId()))));
    }
}
