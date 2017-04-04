package net.synchthia.systera.punishment;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.BungeeUtil;
import net.synchthia.systera.util.DateUtil;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Laica-Lunasys
 */
public class PunishManager {
    private final SysteraPlugin plugin;

    public PunishManager(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    public void broadcast(String level, String name, String reason) {
        Bukkit.broadcastMessage(StringUtil.coloring("&9&lPunish &7[" + level + "]&8≫ &6&l" + name + " &e&lwas Punished for: &a&l" + reason));
    }

    public void action(Player targetPlayer, SysteraProtos.PunishLevel level, String[] messages) {
        if (level.equals(SysteraProtos.PunishLevel.WARN)) {
            for (String s : messages) {
                targetPlayer.sendMessage(s.replaceAll("&", "§"));
            }
        } else {
            String message = StringUtil.someLineToOneLine(messages).replaceAll("&", "§");
            BungeeUtil.disconnect(plugin, targetPlayer, message);
        }
    }

    public static String[] punishMessage(SysteraProtos.PunishEntry punishEntry) {
        return punishMessage(punishEntry.getLevel(), punishEntry.getReason(), punishEntry.getDate(), punishEntry.getExpire());
    }

    public static String[] punishMessage(SysteraProtos.PunishLevel level, String reason, Long date, Long expire) {
        String[] messages = new String[0];
        switch (level) {
            case WARN:
                messages = new String[]{
                        "&8&m[&c&lWARNING&8&m]-----------------------------------",
                        "&c&lYou are WARNED!!",
                        "&e&lReason: &r" + reason,
                        "",
                        "&6&lAppeal at https://sn7.jp/appeal",
                        "&8&m--------------------------------------------",
                };
                break;
            case KICK:
                messages = new String[]{
                        "&c&lYou are Kicked from STARTAIL!!",
                        "&e&lReason: &r" + reason,
                        "",
                        "&6&lAppeal at https://sn7.jp/appeal"
                };
                break;
            case TBAN:
                messages = new String[]{
                        "&c&lYou are Temporary Banned from STARTAIL!!",
                        "&e&lReason: &r" + reason,
                        "&e&lDate: " + DateUtil.epochToDate(date / 1000L),
                        "&7Expire: " + DateUtil.epochToDate(expire / 1000L),
                        "",
                        "&6&lAppeal at https://sn7.jp/appeal"
                };
                break;
            case PBAN:
                messages = new String[]{
                        "&c&lYou are Permanently Banned from STARTAIL!!",
                        "&e&lReason: &r" + reason,
                        "&e&lDate: " + DateUtil.epochToDate(date / 1000L),
                        "&7Expire: " + "&7&l[NEVER]",
                        "",
                        "&6&lAppeal at https://sn7.jp/appeal"
                };
                break;
            default:
                break;
        }
        return messages;
        /*String[] messages = {

        };*/
    }
}
