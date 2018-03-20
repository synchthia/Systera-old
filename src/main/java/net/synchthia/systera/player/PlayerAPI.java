package net.synchthia.systera.player;

import com.google.protobuf.ProtocolStringList;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class PlayerAPI {
    private Map<UUID, PlayerData> localPlayerProfile = new HashMap<>();
    private final SysteraPlugin plugin;

    public PlayerAPI(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    public void clearLocalProfile(UUID playerUUID) {
        localPlayerProfile.remove(playerUUID);
    }

    public PlayerData getLocalProfile(UUID playerUUID) {
        if (!localPlayerProfile.containsKey(playerUUID)) {
            return null;
        }
        return localPlayerProfile.get(playerUUID);
    }

    // For Debug -> getSize
    public Integer localProfileSize() {
        return localPlayerProfile.size();
    }

    // For Debug -> getEntry
    public void debug(UUID playerUUID) {
        System.out.println(playerUUID);
        System.out.println(localPlayerProfile.get(playerUUID).playerUUID);
        System.out.println(localPlayerProfile.get(playerUUID).playerName);
    }

    public CompletableFuture<Boolean> initPlayerProfileAsync(UUID playerUUID, String playerName, String playerAddress, String playerHostname) {
        return plugin.apiClient.initPlayerProfile(playerUUID, playerName, playerAddress, playerHostname).whenComplete((response, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Init Player Profile: Exception threw", throwable);
            }
        });
    }

    public Boolean initPlayerProfile(UUID playerUUID, String playerName, String playerAddress, String playerHostname) throws ExecutionException, InterruptedException, TimeoutException {
        return plugin.apiClient.initPlayerProfile(playerUUID, playerName, playerAddress, playerHostname).get(5, TimeUnit.SECONDS);
    }

    public CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> fetchPlayerProfileAsync(UUID playerUUID, String playerName) {
        return plugin.apiClient.fetchPlayerProfile(playerUUID).whenComplete((response, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Fetch Player Profile: Exception threw", throwable);
                return;
            }

            putLocalPlayerProfile(playerUUID, playerName, response.getEntry());
        });
    }

    public void fetchPlayerProfile(UUID playerUUID, String playerName) throws ExecutionException, InterruptedException, TimeoutException {
        SysteraProtos.FetchPlayerProfileResponse response = plugin.apiClient.fetchPlayerProfile(playerUUID).get(5, TimeUnit.SECONDS);
        if (response != null) {
            putLocalPlayerProfile(playerUUID, playerName, response.getEntry());
        }
    }

    private void putLocalPlayerProfile(UUID playerUUID, String playerName, SysteraProtos.PlayerEntry entry) {
        // Init PlayerData
        PlayerData playerData = new PlayerData();

        // Player UUID/Name
        playerData.playerUUID = playerUUID;
        playerData.playerName = playerName;

        // Player Groups
        playerData.groups = entry.getGroupsList();

        // Player Settings
        playerData.settings.putAll(entry.getSettingsMap());

        // Put PlayerData to Local
        localPlayerProfile.put(playerUUID, playerData);
    }

    public CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> fetchPlayerProfileByName(String playerName) {
        return plugin.apiClient.fetchPlayerProfileByName(playerName).whenComplete(((response, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Fetch Player Profile by Name: Exception threw", throwable);
            }
        }));
    }

    public ProtocolStringList getGroups(UUID playerUUID) {
        if (localPlayerProfile.containsKey(playerUUID)) {
            return localPlayerProfile.get(playerUUID).groups;
        }

        return null;
    }

    public Boolean getSetting(UUID playerUUID, String key) {
        Map<String, Boolean> settings = localPlayerProfile.get(playerUUID).settings;
        return settings.getOrDefault(key, false);
    }

    public CompletableFuture<SysteraProtos.Empty> setPlayerSettings(UUID playerUUID, String key, Boolean value) {
        return plugin.apiClient.setPlayerSettings(playerUUID, key, value).whenComplete(((empty, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Set Player Settings: Exception threw", throwable);
            }
        }));
    }

    public void setPlayerGroups(UUID playerUUID, ProtocolStringList groups) {
        localPlayerProfile.get(playerUUID).groups = groups;
    }

    public class PlayerData {
        public UUID playerUUID;
        public String playerName;
        public ProtocolStringList groups;
        public Map<String, Boolean> settings = new HashMap<>();
    }
}
