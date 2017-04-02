package net.synchthia.systera.player;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * @author Laica-Lunasys
 */
public class TeamManager {
    private final SysteraPlugin plugin;

    public TeamManager(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerTeam(Player player) {
        //String coloredPrefix = PermissionManager.getPrefix(player).replaceAll("&", "§");
        String coloredPrefix = "§7";

        player.setPlayerListName(coloredPrefix + player.getName());

        Scoreboard scoreboard = player.getScoreboard();
        String teamName = player.getName();
        Team getTeam = scoreboard.getTeam(teamName);

        if (getTeam == null) {
            scoreboard.registerNewTeam(teamName);//.setPrefix(coloredPrefix + player.getName());
        }

        scoreboard.getTeam(teamName).setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        scoreboard.getTeam(teamName).addEntry(player.getName());
    }

    public void unregisterTeam(Player player) {
        // Scoreboard
        Scoreboard scoreboard = player.getScoreboard();
        String teamName = player.getName();
        Team getTeam = scoreboard.getTeam(teamName);
        if (getTeam == null) {
            return;
        }
        scoreboard.getTeam(teamName).unregister();
    }
}
