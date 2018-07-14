package net.synchthia.systera.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Laica-Lunasys
 */
@RequiredArgsConstructor
public class ReportCommand extends BaseCommand {
    private final SysteraPlugin plugin;

//    @Command(aliases = {"report", "modreq", "sos"}, desc = "Report to Staff", min = 2, usage = "<target> <message>")
    @CommandAlias("report|modreq|sos|helpop")
    @CommandPermission("systera.command.report")
    @CommandCompletion("@players @punish_reason")
    @Description("Report to Staff")
    public void onReport(CommandSender sender, String target, String message) {
        Player targetPlayer = Bukkit.getPlayer(target);
        if (targetPlayer == null) {
            I18n.sendMessage(sender, "error.player_notfound");
            return;
        }

        UUID toUUID = targetPlayer.getUniqueId();
        String toName = targetPlayer.getName();

        I18n.sendMessage(sender, "report.thanks_report");
        I18n.sendMessage(sender, "report.warning");

        plugin.apiClient.report(Bukkit.getPlayer(sender.getName()).getUniqueId(), sender.getName(), toUUID, toName, message);
    }
}
