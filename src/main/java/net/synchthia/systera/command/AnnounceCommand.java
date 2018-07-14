package net.synchthia.systera.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.AnnounceUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Laica-Lunasys
 */
@RequiredArgsConstructor
public class AnnounceCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("announce")
    @CommandPermission("systera.command.announce")
    @Description("Announce Command")
    public void onAnnounce(CommandSender sender, String message) {
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            AnnounceUtil announceUtil = new AnnounceUtil(onlinePlayer, message);
            announceUtil.sendAnnounce();
        }
    }
}
