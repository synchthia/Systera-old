package net.synchthia.systera.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@CommandAlias("runas")
@CommandPermission("systera.command.runas")
public class RunasCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @Default
    @CatchUnknown
    @Description("Runas Command")
    public void onCommand(CommandSender sender, String target, String command) {
        if (target.equals("all") || target.equals("@a")) {
            sender.sendMessage(
                    ChatColor.GREEN + "Run as " +
                            ChatColor.GOLD + "everyone" +
                            ChatColor.GREEN + ": " +
                            ChatColor.GRAY + command
            );

            plugin.getServer().getOnlinePlayers().forEach(player -> player.performCommand(command));
        } else {
            Player player = Bukkit.getPlayer(target);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player is not online: " + target);
                return;
            }
            sender.sendMessage(
                    ChatColor.GREEN + "Run as " +
                            ChatColor.GOLD + target +
                            ChatColor.GREEN + ": " +
                            ChatColor.GRAY + command
            );
            player.performCommand(command);
        }
    }
}
