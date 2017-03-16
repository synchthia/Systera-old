package net.synchthia.systera.util;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author Nekoneko
 */
public class WorldUtil {
    public static World getMainWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static Location getLobbySpawn() {
        Position position = SysteraPlugin.getInstance().getConfigManager().getSettings().getSpawnPoint().orElse(null);
        if (position == null) {
            return getMainWorld().getSpawnLocation();
        }
        return position.toBukkitLocation().orElse(getMainWorld().getSpawnLocation());
    }
}
