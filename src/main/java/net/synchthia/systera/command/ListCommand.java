package net.synchthia.systera.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.player.VanishManager;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Laica-Lunasys
 */
@RequiredArgsConstructor
public class ListCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("list|who")
    @CommandPermission("systera.command.list")
    @Description("List online players")
    public void onList(CommandSender sender) {
        StringBuilder messages = new StringBuilder();
        Integer count = 0;

        if (sender.hasPermission("systera.command.vanish")) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String nameString = plugin.permissionsAPI.getPrefix(onlinePlayer) + onlinePlayer.getName();
                if (VanishManager.getVanishPlayerInServer().contains(onlinePlayer)) {
                    messages.append(StringUtil.coloring("&e&l[Vanish]" + nameString));
                } else {
                    messages.append(StringUtil.coloring(nameString));
                }
                if (count < Bukkit.getOnlinePlayers().size() - 1) messages.append(ChatColor.WHITE + ", ");
                count = count + 1;
            }
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                messages.append(StringUtil.coloring(plugin.permissionsAPI.getPrefix(onlinePlayer) + onlinePlayer.getName()));
                if (count < Bukkit.getOnlinePlayers().size() - 1) messages.append(ChatColor.WHITE + ", ");
                count = count + 1;
            }
        }

        sender.sendMessage(StringUtil.coloring("&bOnline &6(" + count + "/" + Bukkit.getMaxPlayers() + ")"));
        sender.sendMessage(messages.toString());
    }
}
