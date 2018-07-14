package net.synchthia.systera.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.command.CommandSender;

/**
 * @author Laica-Lunasys
 */
@RequiredArgsConstructor
public class DispatchCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("dispatch")
    @CommandPermission("systera.command.dispatch")
    @Description("Dispatch Command")
    public void onDispatch(CommandSender sender, String target, String command) {
        String commands = command.toString();
        String message = StringUtil.coloring(String.format("&aDispatched: %s >> &6%s", target, commands));

        sender.sendMessage(message);
        plugin.apiClient.dispatch(target, commands);
    }
}
