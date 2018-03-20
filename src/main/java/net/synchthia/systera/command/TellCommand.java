package net.synchthia.systera.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.chat.JapanizeManager;
import net.synchthia.systera.i18n.I18n;
import net.synchthia.systera.player.PlayerAPI;
import net.synchthia.systera.player.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author Laica-Lunasys
 */
public class TellCommand {

    @Command(aliases = {"tell", "msg", "message", "pm", "privatemessage", "w", "whisper"}, desc = "Tell Command", min = 2, usage = "<player> <message>")
    @CommandPermissions("systera.command.tell")
    public static void tell(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        Player target = Bukkit.getPlayer(args.getString(0));
        sendTellMsg(sender, target, args.getJoinedStrings(1));
    }

    @Command(aliases = {"reply", "r"}, desc = "Reply Command", min = 1, usage = "<message>")
    @CommandPermissions("systera.command.tell")
    public static void reply(final CommandContext args, CommandSender sender, SysteraPlugin plugin) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(I18n.getString(sender, "error.invalid_sender"));
        }

        if (!((Player) sender).hasMetadata("reply")) {
            I18n.sendMessage(sender, "error.noreply");
            return;
        }

        Player target = ((Player) ((Player) sender).getMetadata("reply").get(0).value());
        sendTellMsg(sender, target, args.getJoinedStrings(0));
    }

    private static void sendTellMsg(CommandSender sender, Player target, String message) {
        if (target == null || VanishManager.getVanishPlayerInServer().contains(target)) {
            I18n.sendMessage(sender, "error.player_notfound");
            return;
        }

        if ((sender instanceof Player) && SysteraPlugin.getInstance().getPlayerAPI().getSetting(((Player) sender).getUniqueId(), "japanize")) {
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
