package net.synchthia.systera.util;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Laica-Lunasys
 */
public class AnnounceUtil extends BukkitRunnable {
    private BossBar bossBar;

    private Player player;
    private String message;

    private int count;

    public AnnounceUtil(Player player, String message) {
        this.bossBar = SysteraPlugin.getInstance().getServer().createBossBar("", BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        this.player = player;
        this.message = setFormat(message);
        this.count = 0;
    }

    private String setFormat(String message) {
        String space = "                                   ";
        String messageFormat = space + message + "     ";
        return messageFormat;
    }

    public void sendAnnounce() {
        this.runTaskTimerAsynchronously(SysteraPlugin.getInstance(), 0L, 3L);
    }

    @Override
    public void run() {
        Integer limit = 30;
        String start = message.substring(0, 1);
        String end = message.substring(1, message.length());
        String colorcode = "§6§l";

        if (count >= message.length()) {
            bossBar.removeAll();
            this.cancel();
            return;
        }

        //初回はそのまま、その後は最初の文字を最後にくっつける
        if (count >= 1) {
            message = end + start;
        }

        String bossbarMsg = message.substring(0, limit);

        bossBar.setVisible(true);
        bossBar.addPlayer(player);
        bossBar.setTitle(colorcode + bossbarMsg);
        count = count + 1;
    }
}