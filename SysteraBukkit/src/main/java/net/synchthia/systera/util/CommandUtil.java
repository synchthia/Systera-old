package net.synchthia.systera.util;

import net.synchthia.systera.i18n.I18n;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.List;

/**
 * @author Laica-Lunasys
 */
public class CommandUtil extends BukkitCommand {
    public CommandUtil(String name, String description, String usageMessage, String permission, List<String> aliases) {
        super(name);
        this.description = description;
        this.usageMessage = usageMessage;
        this.setPermission(permission);
        this.setAliases(aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!sender.hasPermission(this.getPermission())) {
            I18n.sendMessage(sender, "error.permission");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(this.usageMessage);
            return true;
        }

        return false;
    }
}
