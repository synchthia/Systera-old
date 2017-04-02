package net.synchthia.systera.player;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class VanishManager {
    private static ArrayList<Player> vanishPlayer = new ArrayList<Player>();

    //ｱｯｶﾘｰﾝしているプレイヤーを探してそのプレイヤーを見えなくする
    /* ログインしてきた一般ユーザー が Vanish中のユーザーをhideするために使用 */
    public static ArrayList<Player> getVanishPlayerInServer() {
        return vanishPlayer;
    }

    public static void hideFoundVanishPlayer(Player player) {
        vanishPlayer.forEach(akPlayer -> player.hidePlayer(akPlayer));
    }

    //現在のサーバー内のプレイヤー全員に対してplayerを非表示にする
    public static void applyVanishInServer(Player player, Boolean value) {
        if (value) {
            if (!vanishPlayer.contains(player)) { vanishPlayer.add(player); }
            SysteraPlugin.getInstance().getLogger().log(Level.INFO, "Vanish Applied to: " + player.getName());
            Bukkit.getOnlinePlayers().forEach(players -> { if (!players.hasPermission("systera.command.vanish")) { players.hidePlayer(player); }});
        } else {
            vanishPlayer.remove(player);
            SysteraPlugin.getInstance().getLogger().log(Level.INFO, "Vanish Removed from: " + player.getName());
            Bukkit.getOnlinePlayers().forEach(players -> { if (!players.hasPermission("systera.command.vanish")) { players.showPlayer(player); }});
        }
    }
}