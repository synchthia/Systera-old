package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.AnnounceUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Laica-Lunasys
 */
public class AnnounceCommand {

    @Command(aliases = "announce", desc = "Announce Command", min = 1, usage = "<message>")
    @CommandPermissions("systera.command.announce")
    public static void announce(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        String message = args.getJoinedStrings(0);

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            AnnounceUtil announceUtil = new AnnounceUtil(onlinePlayer, message);
            announceUtil.sendAnnounce();
        }
    }
}
