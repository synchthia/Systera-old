package net.synchthia.systera.player;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class VanishManager {
    private static Set<Player> vanishPlayers = new HashSet<>();

    //ｱｯｶﾘｰﾝしているプレイヤーを探してそのプレイヤーを見えなくする
    /* ログインしてきた一般ユーザー が Vanish中のユーザーをhideするために使用 */
    public static Set<Player> getVanishPlayerInServer() {
        return vanishPlayers;
    }

    public static void hideFoundVanishPlayer(Player player) {
        if (!player.hasPermission("systera.command.vanish")) {
            vanishPlayers.forEach(akPlayer -> player.hidePlayer(akPlayer));
        }
    }

    //現在のサーバー内のプレイヤー全員に対してplayerを非表示にする
    public static void applyVanishInServer(Player player, Boolean value) {
        if (value) {
            if (!vanishPlayers.contains(player)) { vanishPlayers.add(player); }
            SysteraPlugin.getInstance().getLogger().log(Level.INFO, "Vanish Applied to: " + player.getName());
            Bukkit.getOnlinePlayers().stream()
                    .filter(viewer -> !viewer.hasPermission("systera.command.vanish"))
                    .forEach(viewer -> viewer.hidePlayer(player));
        } else {
            vanishPlayers.remove(player);
            SysteraPlugin.getInstance().getLogger().log(Level.INFO, "Vanish Removed from: " + player.getName());
            Bukkit.getOnlinePlayers().stream()
                    .filter(viewer -> !viewer.hasPermission("systera.command.vanish"))
                    .forEach(viewer -> viewer.showPlayer(player));
        }
    }
}