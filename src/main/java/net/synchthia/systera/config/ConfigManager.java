package net.synchthia.systera.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.GsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author Nekoneko
 */
public class ConfigManager {

    private final SysteraPlugin plugin;

    private final Gson gson = new GsonBuilder().create();
    private final File file;

    @Getter
    private Settings settings = new Settings();

    public ConfigManager(SysteraPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "config.json");
        checkOrCreate();
    }

    private void checkOrCreate() {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
                save();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Exception threw while checkOrCreate!", e);
        }
    }

    public void load() {
        checkOrCreate();
        try {
            settings = GsonUtil.load(gson, file, Settings.class);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed load ConfigFile!", e);
        }
    }

    public void save() {
        checkOrCreate();
        try {
            GsonUtil.save(gson, file, settings, Settings.class);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed save ConfigFile!", e);
        }
    }

}
