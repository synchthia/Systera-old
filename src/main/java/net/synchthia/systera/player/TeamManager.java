package net.synchthia.systera.player;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * @author Laica-Lunasys
 */
public class TeamManager {
    public static void registerTeam(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        String teamName = "ST_" + player.getName();
        Team getTeam = scoreboard.getTeam(teamName);

        if (getTeam == null) {
            scoreboard.registerNewTeam(teamName);
        }

        scoreboard.getTeam(teamName).setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        scoreboard.getTeam(teamName).addEntry(player.getName());
    }

    public static void unregisterTeam(Player player) {
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
