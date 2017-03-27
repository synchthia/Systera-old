package net.synchthia.systera.player;

import com.google.protobuf.ProtocolStringList;
import lombok.SneakyThrows;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class PlayerAPI {
    private final SysteraPlugin plugin;

    private static Map<UUID, PlayerData> localPlayerProfile = new HashMap<>();
    private PlayerData playerData;

    public PlayerAPI(SysteraPlugin plugin) {
        this.plugin = plugin;
        this.playerData = new PlayerData();
    }

    public class PlayerData {
        public UUID playerUUID;
        public String playerName;
        public ProtocolStringList groups;
        public Map<String, Boolean> settings = new HashMap<>();
    }

    public Integer localProfileSize() {
        return localPlayerProfile.size();
    }

    public CompletableFuture<Boolean> initPlayerProfile(UUID playerUUID, String playerName, String playerAddress) {
        return plugin.apiClient.initPlayerProfile(playerUUID, playerName, playerAddress).whenComplete((response, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Init Player Profile: Exception threw", throwable);
            }
        });
    }

    @SneakyThrows
    public CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> fetchPlayerProfile(UUID playerUUID, String playerName) {
        return plugin.apiClient.fetchPlayerProfile(playerUUID).whenComplete((response, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Fetch Player Profile: Exception threw", throwable);
                return;
            }

            // Player UUID/Name
            playerData.playerUUID = playerUUID;
            playerData.playerName = playerName;

            // Player Settings
            playerData.settings.putAll(PlayerSettings.getSettingsTemp());

            response.getSettingsMap().forEach((key, value) -> {
                if (playerData.settings.containsKey(key)) {
                    playerData.settings.put(key, value);
                }
            });

            // Put PlayerData to Local
            localPlayerProfile.put(playerUUID, playerData);
        });
    }

    public void clearPlayerProfile(UUID playerUUID) {
        localPlayerProfile.remove(playerUUID);
    }

    public PlayerData getPlayerProfile(UUID playerUUID) {
        if (!localPlayerProfile.containsKey(playerUUID)) {
            return playerData;
        }
        return localPlayerProfile.get(playerUUID);
    }

    public CompletableFuture<SysteraProtos.Empty> setPlayerSettings(UUID playerUUID, String key, Boolean value) {
        localPlayerProfile.get(playerUUID).settings.put(key, value);
        return plugin.apiClient.setPlayerSettings(playerUUID, key, value).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Set Player Settings: Exception threw", throwable);
            }
        }));
    }
}
