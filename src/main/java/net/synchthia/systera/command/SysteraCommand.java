package net.synchthia.systera.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Laica-Lunasys
 */
@RequiredArgsConstructor
public class SysteraCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("systera")
    @CommandPermission("systera.command.systera")
    @Description("Systera Command")
    public void onSystera(CommandSender sender, @Optional String args) {
        if (args != null && args.startsWith("reload")) {
            Bukkit.getPluginManager().disablePlugin(plugin);
            Bukkit.getPluginManager().enablePlugin(plugin);
            sender.sendMessage(ChatColor.GREEN + "Plugin Reloaded.");
            return;
        }

        sender.sendMessage(StringUtil.coloring("&8[&bSystera&8] &7" + plugin.toString()));
    }
}
