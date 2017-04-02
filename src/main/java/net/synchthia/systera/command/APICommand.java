package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.PlayerAPI;
import net.synchthia.systera.player.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * @author Laica-Lunasys
 */
public class APICommand {

    @Command(aliases = "api", desc = "API Command", min = 1)
    @CommandPermissions("systera.command.api")
    public static void api(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        if (args.getString(0).equals("quit")) {
            sender.sendMessage("Quitting stream...");
            plugin.apiClient.quitStream(Bukkit.getServerName());
            return;
        }

        if (args.getString(0).equals("settings")) {
            sender.sendMessage("Get>>" + args.getString(1));
            Boolean s = PlayerAPI.getLocalProfile(Bukkit.getPlayer(sender.getName()).getUniqueId()).settings.get(args.getString(1));
            sender.sendMessage(String.format("%s: %s", args.getString(1), s));
            return;
        }

        if (args.getString(0).equals("shutdown")) {
            sender.sendMessage("Shutdown...");
            try {
                plugin.apiClient.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        if (args.getString(0).equals("localprofile")) {
            sender.sendMessage("Profiles: " + PlayerAPI.localProfileSize());
            return;
        }

        if (args.getString(0).equals("clearlocalprofile")) {
            sender.sendMessage("Clear: " + sender.getName());
            PlayerAPI.clearLocalProfile(Bukkit.getPlayer(sender.getName()).getUniqueId());
            return;
        }

        if (args.getString(0).equals("debugprofile")) {
            PlayerAPI.debug(Bukkit.getPlayer(sender.getName()).getUniqueId());
            return;
        }

        if (args.getString(0).equals("vanish")) {
            int size = VanishManager.getVanishPlayerInServer().size();
            sender.sendMessage("Vanishing: " + size);
            return;
        }
    }
}
