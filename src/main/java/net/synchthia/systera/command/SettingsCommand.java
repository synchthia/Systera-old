package net.synchthia.systera.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.player.PlayerAPI;
import net.synchthia.systera.player.VanishManager;
import net.synchthia.systera.util.MessageUtil;
import net.synchthia.systera.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author Laica-Lunasys
 */
@RequiredArgsConstructor
public class SettingsCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    //    @Command(aliases = {"settings", "setting", "set"}, desc = "Show Player Settings", usage = "<setting> <on/off>")
    @CommandAlias("settings|setting|set")
    @CommandPermission("systera.command.settings")
    @CommandCompletion("@player_settings on|off")
    @Description("Show / Set player settings")
    public void onSettings(CommandSender sender, @Optional String setting, @Optional String mode) {
        if (!(sender instanceof Player)) {
            throw new CommandException(I18n.getString(sender, "error.invalid_sender"));
        }

        Player player = Bukkit.getPlayer(sender.getName());
        PlayerAPI.PlayerData localProfile = plugin.playerAPI.getLocalProfile(player.getUniqueId());
        if (localProfile == null) {
            I18n.sendMessage(sender, "error.localprofile");
            return;
        }

        // /settings
        if (setting == null) {
            sender.sendMessage("§8§m[§b§lSettings§8§m]-----------------------------------");
            sender.sendMessage("§eUsage: /settings <setting> <on/off>");
            Map<String, Boolean> settings = settings(plugin, player);
            if (settings != null) {
                settings.forEach((key, value) -> sender.sendMessage(MessageUtil.settingMsg(key, value)));
            }
            sender.sendMessage("§8§m----------------------------------------------");
            return;
        }

        // /settings hoge
        if (mode == null) {
            if (!localProfile.settings.containsKey(setting)) {
                I18n.sendMessage(sender, "settings.notfound", setting);
                return;
            }
            Boolean value = settings(plugin, player).get(setting);
            sender.sendMessage(MessageUtil.settingMsg(setting, value));
            return;
        }

        // /settings hoge true(false)
        if (!localProfile.settings.containsKey(setting)) {
            I18n.sendMessage(sender, "settings.notfound", setting);
            return;
        }
        Boolean getBoolean = StringUtil.stringSwitch(mode);
        sender.sendMessage(MessageUtil.settingMsg(setting, getBoolean));
        set(plugin, player, setting, getBoolean);
    }

    private static Map<String, Boolean> settings(SysteraPlugin plugin, Player player) {
        PlayerAPI.PlayerData localProfile = plugin.playerAPI.getLocalProfile(player.getUniqueId());
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

    private static void set(SysteraPlugin plugin, Player player, String key, Boolean value) {
        if (key.equals("vanish")) {
            VanishManager.applyVanishInServer(player, value);
        }

        PlayerAPI.PlayerData profile = plugin.playerAPI.getLocalProfile(player.getUniqueId());
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
