package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Laica-Lunasys
 */
public class ReportCommand {
    @Command(aliases = {"report", "modreq", "sos"}, desc = "Report to Staff", min = 2, usage = "<target> <message>")
    @CommandPermissions("systera.command.report")
    public static void report(final CommandContext args, CommandSender sender, SysteraPlugin plugin) {
        Player target = Bukkit.getPlayer(args.getString(0));
        if (target == null) {
            I18n.sendMessage(sender, "error.player_notfound");
            return;
        }

        UUID toUUID = target.getUniqueId();
        String toName = target.getName();

        String message = args.getJoinedStrings(1);

        I18n.sendMessage(sender, "report.thanks_report");
        I18n.sendMessage(sender, "report.warning");

        plugin.apiClient.report(Bukkit.getPlayer(sender.getName()).getUniqueId(), sender.getName(), toUUID, toName, message);
    }
}
