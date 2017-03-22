package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.*;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.command.CommandSender;

/**
 * @author Laica-Lunasys
 */
public class DispatchCommand {

    @Command(aliases = "dispatch", desc = "Dispatch Command", min = 2, usage = "<target> <command>")
    @CommandPermissions("systera.command.dispatch")
    public static void dispatch(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        String target = args.getString(0);
        String commands = args.getJoinedStrings(1);
        String message = StringUtil.coloring(String.format("&aDispatched: %s >> &6%s", target, commands));

        sender.sendMessage(message);
        plugin.apiClient.announce(target, commands);
    }
}
