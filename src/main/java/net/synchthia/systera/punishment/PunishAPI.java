package net.synchthia.systera.punishment;

import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static net.synchthia.api.systera.SysteraProtos.PlayerData;
import static net.synchthia.api.systera.SysteraProtos.PunishLevel;

/**
 * @author Laica-Lunasys
 */
public class PunishAPI {
    private final SysteraPlugin plugin;

    public PunishAPI(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    private class PunishmentData {
        public Boolean available;
        public PunishLevel level;
        public String reason;

        public Long date;
        public Long expire;

        public UUID punishedFromUuid;
        public String punishedFromName;

        public UUID punishedToUuid;
        public String punishedToName;
    }

    public CompletableFuture<SysteraProtos.GetPlayerPunishResponse> lookupPlayer(UUID playerUUID, PunishLevel filterLevel) {
        return plugin.apiClient.getPlayerPunishment(playerUUID, filterLevel, false).whenComplete((value, throwable) -> {
            if (throwable != null) {
                SysteraPlugin.getInstance().getLogger().log(Level.WARNING, "Failed lookup player", throwable);
            }
        });
    }

    public CompletableFuture<SysteraProtos.SetPlayerPunishResponse> punishPlayer(Boolean force, PunishLevel punishLevel, String fromPlayerName, String toPlayerName, String reason, Long expire) {
        String fromPlayerUUID = "";
        String toPlayerUUID = "";

        if (punishLevel.equals(PunishLevel.UNRECOGNIZED)) {
            punishLevel = PunishLevel.PERMBAN;
        }

        // if Executor is player, put UUID.
        if (Bukkit.getPlayer(fromPlayerName) != null) {
            fromPlayerUUID = Bukkit.getPlayer(fromPlayerName).getUniqueId().toString();
        }

        // if target player is online, get UUID.
        // if offline, use remote punishing system.
        Boolean remote;
        if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(toPlayerName))) {
            remote = false;
            toPlayerUUID = Bukkit.getPlayer(toPlayerName).getUniqueId().toString();
        } else {
            remote = true;
        }

        PlayerData from = buildPlayerData(fromPlayerUUID, fromPlayerName);
        PlayerData to = buildPlayerData(toPlayerUUID, toPlayerName);

        return plugin.apiClient.setPlayerPunishment(remote, force, from, to, punishLevel, reason, expire).whenComplete((value, throwable) -> {
            if (throwable != null) {
                SysteraPlugin.getInstance().getLogger().log(Level.WARNING, "Failed punish player", throwable);
            }
        });
    }

    public PlayerData buildPlayerData(String playerUUID, String playerName) {
        PlayerData data = PlayerData.newBuilder()
                .setUUID(playerUUID.replaceAll("-", ""))
                .setName(playerName)
                .build();
        return data;
    }
}
