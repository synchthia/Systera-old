package net.synchthia.systera.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author Laica-Lunasys
 */
@RequiredArgsConstructor
public class SpawnCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("spawn")
    @CommandPermission("systera.command.spawn")
    @Description("Teleport spawn location")
    public void onSpawn(CommandSender sender) {
        if (!(sender instanceof Player)) {
            throw new CommandException(I18n.getString(sender, "error.invalid_sender"));
        }

        Player player = (Player) sender;
        Optional<Location> loc = plugin.spawnManager.getSpawnLocation(false, player.getWorld());
        loc.ifPresent(player::teleport);
    }

    @CommandAlias("setspawn")
    @CommandPermission("systera.command.setspawn")
    @Description("Set spawn location")
    public void onSetSpawn(CommandSender sender, @co.aikar.commands.annotation.Optional String flag) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(I18n.getString(sender, "error.invalid_sender"));
        }

        Player player = (Player) sender;
        if (flag != null && flag.equals("-s")) {
            plugin.spawnManager.setSpawnLocation(true, player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Set Spawn location! (Always Spawn when Login)");
            return;
        }

        plugin.spawnManager.setSpawnLocation(false, player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Set Spawn location!");
    }
}
