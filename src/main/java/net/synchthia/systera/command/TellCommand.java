package net.synchthia.systera.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import lombok.RequiredArgsConstructor;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.chat.JapanizeManager;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.player.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author Laica-Lunasys
 */
@RequiredArgsConstructor
public class TellCommand extends BaseCommand {
    private final SysteraPlugin plugin;

    @CommandAlias("tell|msg|message|pm|privatemessage|w|whisper")
    @CommandPermission("systera.command.tell")
    @CommandCompletion("@players")
    @Description("Tell Command")
    public void onTell(CommandSender sender, String player, String message) {
        Player target = Bukkit.getPlayer(player);
        sendTellMsg(sender, target, message);
    }

    @CommandAlias("reply|r")
    @CommandPermission("systera.command.tell")
    @Description("Reply Command")
    public void onReply(CommandSender sender, String message) {
        if (!(sender instanceof Player)) {
            throw new CommandException(I18n.getString(sender, "error.invalid_sender"));
        }

        if (!((Player) sender).hasMetadata("reply")) {
            I18n.sendMessage(sender, "error.noreply");
            return;
        }

        Player target = ((Player) ((Player) sender).getMetadata("reply").get(0).value());
        sendTellMsg(sender, target, message);
    }

    private static void sendTellMsg(CommandSender sender, Player target, String message) {
        if (target == null || VanishManager.getVanishPlayerInServer().contains(target)) {
            I18n.sendMessage(sender, "error.player_notfound");
            return;
        }

        if ((sender instanceof Player) && SysteraPlugin.getInstance().playerAPI.getSetting(((Player) sender).getUniqueId(), "japanize")) {
            JapanizeManager japanize = new JapanizeManager();
            String converted = japanize.convert(message);
            if (converted != null && !converted.isEmpty()) {
                message = message + "§6 (" + converted + "§6)";
            }
        }

        String toFormat = "§c§l[Private] §6§lTo {player}: §7{message}"
                .replace("{player}", target.getDisplayName())
                .replace("{message}", message);

        String fromFormat = "§c§l[Private] §6§lFrom {player}: §7{message}"
                .replace("{player}", sender.getName())
                .replace("{message}", message);

        sender.sendMessage(toFormat);
        target.sendMessage(fromFormat);

        // プレイヤーからの場合のみメタデータをセットする
        if ((sender instanceof Player)) {
            target.setMetadata("reply", new FixedMetadataValue(SysteraPlugin.getInstance(), sender));
        }
    }
}
