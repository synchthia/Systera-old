package net.synchthia.systera.gate;

import com.google.gson.Gson;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

/**
 * @author Laica-Lunasys
 */
public class GateManager {
    private final JavaPlugin plugin;
    private final File file;
    private static Gson gson = new Gson();

    private Map<String, GateData> gates = new HashMap<>();

    public GateManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "gates.json");

        if (file.exists()) {
            try {
                load();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed load data", e);
            }
        }
    }

    private void load() throws IOException {
        GateData[] datas;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            datas = gson.fromJson(br, GateData[].class);
        }

        gates.clear();

        for (GateData gate : datas) {
            if (gate.getName() == null) {
                continue;
            }
            gates.put(gate.getName(), gate);
        }
    }

    public void save() throws IOException {
        String json = gson.toJson(gates.values());

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(json);
            fw.flush();
        }
    }

    public boolean add(@NonNull GateData gate) {
        Optional<GateData> result = findGates().stream()
                .filter(v -> gate.getGateRange() == v.getGateRange() || gate.getName().equals(v.getName()))
                .findFirst();

        if (result.isPresent()) {
            // Already Defined
            return false;
        }

        gates.put(gate.getName(), gate);
        return true;
    }

    public boolean remove(@NonNull String key) {
        if (gates.containsKey(key)) {
            gates.remove(key);
            return true;
        }
        return false;
    }

    public Collection<GateData> findGates() {
        return gates.values();
    }

    public GateData findGate(@NonNull String key) {
        return gates.get(key);
    }

    public Optional<GateData> inPortal(@NonNull Location loc) {
        return findGates().stream()
                .filter(v -> loc.getWorld().getUID().toString().equals(v.getWorldID().toString()))
                .filter(v -> loc.getBlockX() >= v.getPortalRange().getMin()[0] && loc.getBlockX() <= v.getPortalRange().getMax()[0])
                .filter(v -> loc.getBlockY() >= v.getPortalRange().getMin()[1] && loc.getBlockY() <= v.getPortalRange().getMax()[1])
                .filter(v -> loc.getBlockZ() >= v.getPortalRange().getMin()[2] && loc.getBlockZ() <= v.getPortalRange().getMax()[2])
                .findFirst();
    }

    public Optional<GateData> inGate(@NonNull Location loc) {
        return findGates().stream()
                .filter(v -> loc.getWorld().getUID().toString().equals(v.getWorldID().toString()))
                .filter(v -> loc.getBlockX() >= v.getGateRange().getMin()[0] && loc.getBlockX() <= v.getGateRange().getMax()[0])
                .filter(v -> loc.getBlockY() >= v.getGateRange().getMin()[1] && loc.getBlockY() <= v.getGateRange().getMax()[1])
                .filter(v -> loc.getBlockZ() >= v.getGateRange().getMin()[2] && loc.getBlockZ() <= v.getGateRange().getMax()[2])
                .findFirst();
    }

    public Optional<GateData> inSign(@NonNull Block block) {
        Location loc = block.getLocation();
        return findGates().stream()
                .filter(v -> block.getWorld().getUID().toString().equals(v.getWorldID().toString()))
                .filter(v -> loc.getBlockX() == v.getSignX())
                .filter(v -> loc.getBlockY() == v.getSignY())
                .filter(v -> loc.getBlockZ() == v.getSignZ())
                .findFirst();
    }
}
