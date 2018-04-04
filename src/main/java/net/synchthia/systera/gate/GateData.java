package net.synchthia.systera.gate;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.Location;

import java.util.UUID;

/**
 * @author Laica-Lunasys
 */
@Data
public class GateData {
    private final String destination;
    private final UUID worldID;
    // Gate Range
    private final Range gateRange;
    // Portal Range
    private final Range portalRange;
    // Sign Location
    private final Integer signX, signY, signZ;
    @NonNull
    private String name;

    public GateData(@NonNull String name, String destination, Location signLoc, Range gateRange, Range portalRange) {
        this.name = name;
        this.destination = destination;
        this.worldID = signLoc.getWorld().getUID();

        this.signX = signLoc.getBlockX();
        this.signY = signLoc.getBlockY();
        this.signZ = signLoc.getBlockZ();

        this.gateRange = gateRange;
        this.portalRange = portalRange;
    }

    @Data
    public static class Range {
        private final double[] min;
        private final double[] max;

        Range(Location min, Location max) {
            this.min = new double[]{min.getX(), min.getY(), min.getZ()};
            this.max = new double[]{max.getX(), max.getY(), max.getZ()};
        }
    }
}