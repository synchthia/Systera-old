package net.synchthia.systera;

import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.synchthia.systera.player.PlayerListener;

/**
 * @author Laica-Lunasys
 */
public class SysteraBungee extends Plugin {
    public static SysteraBungee plugin;

    @Getter
    public APIClient apiClient;
    private String apiServerAddress = "192.168.100.53:17300";

    @Override
    public void onEnable() {
        plugin = this;

        apiClient = new APIClient(apiServerAddress);

        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        pm.registerListener(this, new PlayerListener());
    }

    @Override
    @SneakyThrows
    public void onDisable() {
        apiClient.shutdown();
    }
}
