package net.synchthia.systera.player;

import com.google.protobuf.ProtocolStringList;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.SysteraPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class PlayerAPI {
    private final SysteraPlugin plugin;

    private static HashMap<UUID, PlayerData> localPlayerProfile = new HashMap<>();
    private PlayerData playerData;

    public PlayerAPI(SysteraPlugin plugin) {
        this.plugin = plugin;
        this.playerData = new PlayerData();
    }

    public class PlayerData {
        public UUID playerUUID;
        public String playerName;
        public ProtocolStringList groups;
        public HashMap<String, Boolean> settings = new HashMap<String, Boolean>();
    }

    public CompletableFuture<Boolean> initPlayerProfile(UUID playerUUID, String playerName, String playerAddress) {
        return plugin.apiClient.initPlayerProfile(playerUUID, playerName, playerAddress).whenComplete((response, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Init Player Profile: Exception threw", throwable);
            }
        });
    }

    public CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> fetchPlayerProfile(UUID playerUUID, String playerName) {
        return plugin.apiClient.fetchPlayerProfile(playerUUID).whenComplete((value, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "Failed Init Player Profile: Exception threw", throwable);
            }

            playerData.playerUUID = playerUUID;
            playerData.playerName = playerName;
            playerData.settings.putAll(value.getSettingsMap());

            localPlayerProfile.put(playerUUID, playerData);
        });
    }
}
