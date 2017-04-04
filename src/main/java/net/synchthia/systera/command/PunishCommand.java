package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.punishment.PunishManager;
import net.synchthia.systera.util.DateUtil;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class PunishCommand {
    @Command(aliases = "warn", desc = "Warning Command", min = 2, usage = "<player> <reason>")
    @CommandPermissions("systera.command.punishment")
    public static void warn(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        String toPlayerName = args.getString(0);
        String reason = args.getJoinedStrings(1);

        punish(false, SysteraProtos.PunishLevel.WARN, sender, toPlayerName, reason, 0L);
    }

    @Command(aliases = "kick", desc = "Kick Command", min = 2, usage = "<player> <reason>")
    @CommandPermissions("systera.command.punishment")
    public static void kick(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        String toPlayerName = args.getString(0);
        String reason = args.getJoinedStrings(1);

        punish(false, SysteraProtos.PunishLevel.KICK, sender, toPlayerName, reason, 0L);
    }

    @Command(aliases = {"tempban", "tban", "punish"}, desc = "Temporary BAN Command", min = 2, usage = "<player> [expire] <reason>")
    @CommandPermissions("systera.command.punishment")
    public static void tempBan(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        String toPlayerName = args.getString(0);
        String expireDate = args.argsLength() < 3 ? "7d" : args.getString(1);
        String reason = args.getJoinedStrings(args.argsLength() - 1);

        try {
            Long expire = DateUtil.getEpochMilliTime() + DateUtil.parseDateString(expireDate);
            punish(false, SysteraProtos.PunishLevel.TBAN, sender, toPlayerName, reason, expire);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid Expire Date!");
        }
    }
    
    @Command(aliases = {"permban", "pban", "ppunish"}, flags = "f", desc = "Permanently BAN Command", min = 2, usage = "<player> <reason>")
    @CommandPermissions("systera.command.punishment")
    public static void permBan(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        Boolean force = false;
        String toPlayerName = args.getString(0);
        String reason = args.getJoinedStrings(1);

        if (args.hasFlag('f')) {
            force = true;
        }

        punish(force, SysteraProtos.PunishLevel.PBAN, sender, toPlayerName, reason, 0L);
    }

    private static void punish(Boolean force, SysteraProtos.PunishLevel level, CommandSender sender, String toPlayerName, String reason, Long expire) {
        SysteraPlugin plugin = SysteraPlugin.getInstance();
        plugin.punishAPI.punishPlayer(force, level, sender.getName(), toPlayerName, reason, expire).whenComplete((response, throwable) -> {
            if (throwable != null) {
                sender.sendMessage(ChatColor.RED + "Throwable detected @ Player Punishment");
                plugin.getLogger().log(Level.SEVERE, "Throwable detected @ Player Punishment", throwable);
                return;
            }

            if (!force && response.getNoprofile()) {
                I18n.sendMessage(sender, "punishment.error_noprofile", toPlayerName);
                return;
            }

            if (!force && level.getNumber() < SysteraProtos.PunishLevel.TBAN.getNumber() && response.getOffline()) {
                I18n.sendMessage(sender, "error.player_offline", toPlayerName);
                return;
            }

            if (response.getDuplicate()) {
                I18n.sendMessage(sender, "punishment.error_duplicate", toPlayerName);
                return;
            }

            if (response.getCooldown()) {
                I18n.sendMessage(sender, "punishment.error_cooldown", toPlayerName);
                return;
            }

            sender.sendMessage(StringUtil.coloring("&9&lPunish &7[" + level.toString() + "]&8≫ &e&lPunished &6&l" + toPlayerName + " &e&l: &a&l" + reason));

            Player targetPlayer = Bukkit.getPlayer(toPlayerName);
            if (targetPlayer == null) {
                return;
            }

            String[] messages = PunishManager.punishMessage(level, reason, DateUtil.getEpochMilliTime(), expire);
            PunishManager punishManager = new PunishManager(plugin);
            punishManager.broadcast(level.toString(), targetPlayer.getName(), reason);
            punishManager.action(targetPlayer, level, messages);
        });
    }
}
