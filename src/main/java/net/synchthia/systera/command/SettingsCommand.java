package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.player.PlayerAPI;
import net.synchthia.systera.player.VanishManager;
import net.synchthia.systera.util.MessageUtil;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

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
            settings(player).forEach((key, value) -> sender.sendMessage(MessageUtil.settingMsg(key, value)));
            sender.sendMessage("§8§m----------------------------------------------");
            return;
        }

        // /settings hoge
        if (args.argsLength() == 1) {
            if (!localProfile.settings.containsKey(args.getString(0))) {
                I18n.sendMessage(sender, "settings.notfound", args.getString(0));
                return;
            }
            Boolean value = settings(player).get(args.getString(0));
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
            sender.sendMessage(MessageUtil.settingMsg(args.getString(0), getBoolean));
            set(player, args.getString(0), getBoolean);
            return;
        }
    }

    private static Map<String, Boolean> settings(Player player) {
        PlayerAPI.PlayerData localProfile = PlayerAPI.getLocalProfile(player.getUniqueId());
        if (localProfile == null) {
            I18n.sendMessage(player, "error.localprofile");
            return null;
        }
        if (!player.hasPermission("systera.command.settings.staff")) {
            Map<String, Boolean> s = localProfile.settings;
            s.remove("vanish");
        }
        return localProfile.settings;
    }

    private static void set(Player player, String key, Boolean value) {
        if (key.equals("vanish")) {
            VanishManager.applyVanishInServer(player, value);
        }

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
            profile.settings.put(key, value);
        }));
    }
}
