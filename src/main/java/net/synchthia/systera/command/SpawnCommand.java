package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.util.Position;
import net.synchthia.systera.util.WorldUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Laica-Lunasys
 */
public class SpawnCommand {

    @Command(aliases = "spawn", desc = "Teleport Spawn location")
    @CommandPermissions("systera.command.spawn")
    public static void spawn(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(I18n.getString(sender, "error.invalid_sender"));
        }

        Player player = (Player) sender;

        player.teleport(WorldUtil.getLobbySpawn());
    }

    @Command(aliases = "setspawn", desc = "Set spawn")
    @CommandPermissions("systera.command.setspawn")
    public static void setspawn(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(I18n.getString(sender, "error.invalid_sender"));
        }

        Player player = (Player) sender;

        plugin.getConfigManager().getSettings().setSpawnPoint(new Position(player.getLocation()));
        player.sendMessage(ChatColor.GREEN + "Set Spawn location!");
    }
}
