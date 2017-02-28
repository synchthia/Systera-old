package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Laica-Lunasys
 */
public class SysteraCommand {

    @Command(aliases = "systera", desc = "Systera Command")
    @CommandPermissions("systera.command.punishment")
    public static void systera(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        if (args.argsLength() == 1 && args.getString(0).equals("reload")) {
            Bukkit.getPluginManager().disablePlugin(plugin);
            Bukkit.getPluginManager().enablePlugin(plugin);
            sender.sendMessage(ChatColor.GREEN + "Plugin Reloaded.");
            return;
        }

        sender.sendMessage(StringUtil.coloring("&8[&bSystera&8] &7" + plugin.toString()));
    }
}
