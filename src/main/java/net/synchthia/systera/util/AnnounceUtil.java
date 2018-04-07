package net.synchthia.systera.util;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.Bukkit;
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
    private final String fullMessage;
    private BossBar bossBar;
    private Player player;
    private String message;
    private int count;
    private int strLen;
    private int limit;
    private int dismissTime;

    private String colorCode = "";

    public AnnounceUtil(Player player, String message) {
        this.bossBar = SysteraPlugin.getInstance().getServer().createBossBar("", BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        this.player = player;
        this.message = setFormat(message);
        this.fullMessage = setFormat(message);
        this.count = 0;
        this.strLen = setFormat(message).length();
        this.limit = 30;
        this.dismissTime = 5;
    }

    private String setFormat(String message) {
        String space = "                                   ";
        String messageFormat = space + message + "          ";
        return messageFormat;
    }

    public void sendAnnounce() {
        this.runTaskTimerAsynchronously(SysteraPlugin.getInstance(), 0L, 3L);
    }

    @Override
    public void run() {
        if (count >= strLen || dismissTime == 0) {
            bossBar.removeAll();
            this.cancel();
            return;
        }

        int startColorCnt = checkColors(false, message);

        // Has Color Code
        String dispStart = message.substring(0, startColorCnt + 1); // [T]EST OR [&4T]EST OR [&4&cT]EST
        String dispEnd = message.substring(startColorCnt + 1, message.length()); // T[EST]
        if (startColorCnt > 0) {
            colorCode = dispStart.substring(0, startColorCnt);
        }

        if (message.substring(limit - 1, limit).startsWith("&")) {
            int offset = checkColors(false, message.substring(limit - 1));
            limit += offset;
            count += offset;
        }

        String bossbarMsg = message;
        if (message.length() > limit) {
            bossbarMsg = message.substring(0, limit);
        }

        bossBar.setVisible(true);
        bossBar.addPlayer(player);
        bossBar.setTitle(StringUtil.coloring(colorCode + bossbarMsg));
        Bukkit.getConsoleSender().sendMessage(StringUtil.coloring("[" + bossbarMsg + "]" + "(" + limit + ")"));
        message = dispEnd + dispStart;

        // Reset Limit
        limit -= startColorCnt;

        count = count + 1;
    }

    private int checkColors(boolean reverse, String str) {
        if (reverse) { // END -> START
            // str = &c&lTEST
            int colors = 0;
            int pos = str.length();
            // [&]c&lTEST
            while (str.substring((pos - colors) - 2, (pos - colors)).startsWith("&")) {
                colors += 2;
            }
            return colors;
        } else { // START -> END
            // str = &c&lTEST
            int colors = 0;
            // [&]c&lTEST
            while (str.substring(colors, colors + 1).equals("&")) {
                colors += 2;
            }
            return colors;
        }
    }
}