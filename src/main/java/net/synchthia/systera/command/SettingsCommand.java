package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.*;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.player.PlayerAPI;
import net.synchthia.systera.player.PlayerSettings;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * @author Laica-Lunasys
 */
public class SettingsCommand {

    @Command(aliases = {"settings", "setting", "set"}, desc = "Show Player Settings", usage = "<setting> <on/off>")
    @CommandPermissions("systera.command.settings")
    public static void settings(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(I18n.getString(sender, "error.invalid_sender"));
        }

        Player player = Bukkit.getPlayer(sender.getName());
        Map<String, Boolean> playerSettings = plugin.playerAPI.getPlayerProfile(player.getUniqueId()).settings;

        // /settings
        if (args.argsLength() == 0) {
            sender.sendMessage("§8§m[§b§lSettings§8§m]-----------------------------------");
            sender.sendMessage("§eUsage: /settings <setting> <on/off>");
            playerSettings.forEach((getKey, getValue) -> sender.sendMessage(settingMsg(getKey, getValue)));
            sender.sendMessage("§8§m----------------------------------------------");
            return;
        }

        // /settings hoge
        if (args.argsLength() == 1) {
            if (!playerSettings.containsKey(args.getString(0))) {
                I18n.sendMessage(sender, "settings.notfound", args.getString(0));
                return;
            }
            Boolean value = playerSettings.get(args.getString(0));
            sender.sendMessage(settingMsg(args.getString(0), value));
            return;
        }

        // /settings hoge true(false)
        if (args.argsLength() == 2) {
            if (!PlayerSettings.getSettingsTemp().containsKey(args.getString(0))) {
                I18n.sendMessage(sender, "settings.notfound", args.getString(0));
                return;
            }
            Boolean getBoolean = StringUtil.stringSwitch(args.getString(1));
            set(Bukkit.getPlayer(sender.getName()), args.getString(0), getBoolean);
            return;
        }
    }

    private static void set(Player player, String key, Boolean value) {
        SysteraPlugin.getInstance().playerAPI.setPlayerSettings(player.getUniqueId(), key, value).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                I18n.sendMessage(player, "error.api", "Set Player Settings");
                return;
            }
            player.sendMessage(settingMsg(key, value));
        }));
    }

    private static String settingMsg(String key, Boolean value) {
        String flag;
        if (value) {
            flag = "§aON";
        } else {
            flag = "§cOFF";
        }
        return String.format("§6%s: %s", key, flag);
    }

}
