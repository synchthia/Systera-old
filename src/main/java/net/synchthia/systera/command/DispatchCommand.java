package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.command.CommandSender;

/**
 * @author Laica-Lunasys
 */
public class DispatchCommand {

    @Command(aliases = "dispatch", desc = "Dispatch Command", min = 1, usage = "<command>")
    @CommandPermissions("systera.command.dispatch")
    public static void dispatch(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        sender.sendMessage("Issued: " + args.getJoinedStrings(0));
        plugin.apiClient.announce(args.getJoinedStrings(0));
    }
}
