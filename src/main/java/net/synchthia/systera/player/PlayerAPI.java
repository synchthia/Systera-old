package net.synchthia.systera.player;

import com.google.protobuf.ProtocolStringList;
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

    public PlayerAPI(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    public class PlayerData {
        public UUID playerUUID;
        public String playerName;
        public ProtocolStringList groups;
        public Map<String, Boolean> settings = new HashMap<>();
    }

    public CompletableFuture<Boolean> initPlayerProfile(UUID playerUUID, String playerName, String playerAddress) {
        return plugin.apiClient.initPlayerProfile(playerUUID, playerName, playerAddress).whenComplete((response, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Init Player Profile: Exception threw", throwable);
            }
        });
    }

    public CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> fetchPlayerProfile(UUID playerUUID, String playerName) {
        return plugin.apiClient.fetchPlayerProfile(playerUUID).whenComplete((response, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Fetch Player Profile: Exception threw", throwable);
                return;
            }

            // Init PlayerData
            PlayerData playerData = new PlayerData();

            // Player UUID/Name
            playerData.playerUUID = playerUUID;
            playerData.playerName = playerName;

            // Player Groups
            playerData.groups = response.getGroupsList();

            // Player Settings
            playerData.settings.putAll(PlayerSettings.getSettingsTemp());
            response.getSettingsMap().forEach((key, value) -> {
                if (PlayerSettings.getSettingsTemp().containsKey(key)) {
                    playerData.settings.put(key, value);
                }
            });

            // Put PlayerData to Local
            localPlayerProfile.put(playerUUID, playerData);
        });
    }

    public CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> fetchPlayerProfileByName(String playerName) {
        return plugin.apiClient.fetchPlayerProfileByName(playerName).whenComplete(((response, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Fetch Player Profile by Name: Exception threw", throwable);
            }
        }));
    }

    public static ProtocolStringList getGroups(UUID playerUUID) {
        ProtocolStringList groups = localPlayerProfile.get(playerUUID).groups;

        return groups;
    }

    public static Boolean getSetting(UUID playerUUID, String key) {
        Map<String, Boolean> settings = localPlayerProfile.get(playerUUID).settings;
        if (!settings.containsKey(key)) {
            return null;
        }
        return settings.get(key);
    }

    public CompletableFuture<SysteraProtos.Empty> setPlayerSettings(UUID playerUUID, String key, Boolean value) {
        return plugin.apiClient.setPlayerSettings(playerUUID, key, value).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Set Player Settings: Exception threw", throwable);
            }
        }));
    }

    public static void clearLocalProfile(UUID playerUUID) {
        localPlayerProfile.remove(playerUUID);
    }

    public static PlayerData getLocalProfile(UUID playerUUID) {
        if (!localPlayerProfile.containsKey(playerUUID)) {
            return null;
        }
        return localPlayerProfile.get(playerUUID);
    }

    // For Debug -> getSize
    public static Integer localProfileSize() {
        return localPlayerProfile.size();
    }

    // For Debug -> getEntry
    public static void debug(UUID playerUUID) {
        System.out.println(playerUUID);
        System.out.println(localPlayerProfile.get(playerUUID).playerUUID);
        System.out.println(localPlayerProfile.get(playerUUID).playerName);
    }
}
