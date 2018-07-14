package net.synchthia.systera.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.util.DateUtil;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
@RequiredArgsConstructor
public class PunishCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("warn")
    @CommandPermission("systera.command.punishment")
    @CommandCompletion("@players @punish_reason")
    @Description("Warning Command")
    public void onWarn(CommandSender sender, String player, String reason) {
        punish(false, SysteraProtos.PunishLevel.WARN, sender, player, reason, 0L);
    }

    @CommandAlias("kick")
    @CommandPermission("systera.command.punishment")
    @CommandCompletion("@players @punish_reason")
    @Description("Kick Command")
    public void onKick(CommandSender sender, String player, String reason) {
        punish(false, SysteraProtos.PunishLevel.KICK, sender, player, reason, 0L);
    }

    //    @Command(aliases = {"tempban", "tban", "punish"}, flags = "t:t", desc = "Temporary BAN Command", min = 2, usage = "<player> <reason>")
    @CommandAlias("tempban|tban|punish")
    @CommandPermission("systera.command.punishment")
    @CommandCompletion("@players @punish_reason")
    public void onTempBan(CommandSender sender, String player, String reason) {
//        String expireDate = args.hasFlag('t') ? args.getFlag('t') : "7d";
        String expireDate = "7d";

        try {
            Long expire = DateUtil.getEpochMilliTime() + DateUtil.parseDateString(expireDate);
            punish(false, SysteraProtos.PunishLevel.TEMPBAN, sender, player, reason, expire);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid Expire Date!");
        }
    }

    //    @Command(aliases = {"permban", "pban", "ppunish"}, flags = "f", desc = "Permanently BAN Command", min = 2, usage = "<player> <reason>")
    @CommandAlias("permban|pban|ppunish")
    @CommandPermission("systera.command.punishment")
    @CommandCompletion("@players @punish_reason")
    public void onPermBan(CommandSender sender, String player, @Optional String force, String reason) {
        Boolean isForce = false;
        if (force != null && force.equals("-f")) {
            isForce = true;
        }

        punish(isForce, SysteraProtos.PunishLevel.PERMBAN, sender, player, reason, 0L);
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

            if (response.getDuplicate()) {
                I18n.sendMessage(sender, "punishment.error_duplicate", toPlayerName);
                return;
            }

            if (response.getCooldown()) {
                I18n.sendMessage(sender, "punishment.error_cooldown", toPlayerName);
                return;
            }

            if (!force && level.getNumber() < SysteraProtos.PunishLevel.TEMPBAN.getNumber() && response.getOffline()) {
                I18n.sendMessage(sender, "error.player_offline", toPlayerName);
                return;
            }

            sender.sendMessage(StringUtil.coloring("&9&lPunish &7[" + level.toString() + "]&8≫ &e&lPunished &6&l" + toPlayerName + " &e&l: &a&l" + reason));
        });
    }
}
