package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.player.PlayerAPI;
import net.synchthia.systera.util.MessageUtil;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Laica-Lunasys
 */
public class SettingsCommand {

    @Command(aliases = {"settings", "setting", "set"}, desc = "Show Player Settings", usage = "<setting> <on/off>")
    // Unnecessary? -> @CommandPermissions("systera.command.settings")
    public static void settings(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(I18n.getString(sender, "error.invalid_sender"));
        }

        Player player = Bukkit.getPlayer(sender.getName());
        PlayerAPI.PlayerData localProfile = PlayerAPI.getLocalProfile(player.getUniqueId());
        if (localProfile == null) {
            I18n.sendMessage(sender, "error.localprofile");
            return;
        }

        // /settings
        if (args.argsLength() == 0) {
            sender.sendMessage("§8§m[§b§lSettings§8§m]-----------------------------------");
            sender.sendMessage("§eUsage: /settings <setting> <on/off>");
            localProfile.settings.forEach((getKey, getValue) -> sender.sendMessage(MessageUtil.settingMsg(getKey, getValue)));
            sender.sendMessage("§8§m----------------------------------------------");
            return;
        }

        // /settings hoge
        if (args.argsLength() == 1) {
            if (!localProfile.settings.containsKey(args.getString(0))) {
                I18n.sendMessage(sender, "settings.notfound", args.getString(0));
                return;
            }
            Boolean value = get(player, args.getString(0));
            sender.sendMessage(MessageUtil.settingMsg(args.getString(0), value));
            return;
        }

        // /settings hoge true(false)
        if (args.argsLength() == 2) {
            if (!localProfile.settings.containsKey(args.getString(0))) {
                I18n.sendMessage(sender, "settings.notfound", args.getString(0));
                return;
            }
            Boolean getBoolean = StringUtil.stringSwitch(args.getString(1));
            set(player, args.getString(0), getBoolean);
            sender.sendMessage(MessageUtil.settingMsg(args.getString(0), getBoolean));
            return;
        }
    }

    private static Boolean get(Player player, String key) {
        PlayerAPI.PlayerData profile = PlayerAPI.getLocalProfile(player.getUniqueId());
        if (profile == null) {
            I18n.sendMessage(player, "error.localprofile");
            return false;
        }
        return profile.settings.get(key);
    }

    private static void set(Player player, String key, Boolean value) {
        PlayerAPI.PlayerData profile = PlayerAPI.getLocalProfile(player.getUniqueId());
        if (profile == null) {
            I18n.sendMessage(player, "error.localprofile");
            return;
        }
        SysteraPlugin.getInstance().playerAPI.setPlayerSettings(player.getUniqueId(), key, value).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                I18n.sendMessage(player, "error.api", "Set Player Settings");
                return;
            }
            player.sendMessage(MessageUtil.settingMsg(key, value));
            profile.settings.put(key, value);
        }));
    }
}
