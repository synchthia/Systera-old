package net.synchthia.systera.location;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class SpawnManager {
    private static Gson gson = new Gson();
    private final JavaPlugin plugin;
    private final File file;
    private Map<String, LocationData> locations = new HashMap<>();

    public SpawnManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "spawn.json");

        if (file.exists()) {
            try {
                load();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed load data", e);
            }
        }
    }

    public static Location convert(LocationData locationData) {
        return new Location(
                Bukkit.getWorld(locationData.getWorldName()),
                locationData.getX(),
                locationData.getY(),
                locationData.getZ(),
                locationData.getYaw(),
                locationData.getPitch()
        );
    }

    private void load() throws IOException {
        LocationData[] datas;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            datas = gson.fromJson(br, LocationData[].class);
        }

        locations.clear();

        for (LocationData loc : datas) {
            if (loc.getWorldName() == null) {
                continue;
            }
            locations.put(loc.getWorldName(), loc);
        }
    }

    public void save() throws IOException {
        String json = gson.toJson(locations.values());

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(json);
            fw.flush();
        }
    }

    public Optional<Location> getSpawnLocation(boolean whenLogin, World currentWorld) {
        if (whenLogin) {
            Optional<LocationData> optLoc = locations.values().stream()
                    .filter(v -> v.isWhenLogin())
                    .findFirst();
            if (optLoc.isPresent()) {
                return Optional.of(convert(optLoc.get()));
            }
            return Optional.empty();
        }

        // Is exist current world
        if (locations.containsKey(currentWorld.getName())) {
            return Optional.of(convert(locations.get(currentWorld.getName())));
        }

        World defaultWorld = plugin.getServer().getWorlds().get(0);

        // Default World
        if (locations.containsKey(defaultWorld.getName())) {
            return Optional.of(convert(locations.get(defaultWorld.getName())));
        } else {
            return Optional.of(defaultWorld.getSpawnLocation().add(0.5, 0, 0.5));
        }
    }

    public void setSpawnLocation(boolean whenLogin, Location l) {
        LocationData newLoc = new LocationData(l.getWorld().getName(), whenLogin, l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());

        if (whenLogin) {
            locations.values().stream()
                    .filter(v -> v.isWhenLogin())
                    .forEach(v -> v.setWhenLogin(false));
        }

        locations.put(newLoc.getWorldName(), newLoc);
    }
}
