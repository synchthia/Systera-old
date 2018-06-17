package net.synchthia.systera.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockUtil {
    public static Location getClickedLocation(Block block, BlockFace blockFace) {
        Location loc = block.getLocation();

        switch (blockFace) {
            case UP:
                loc.add(0, 1, 0);
                break;
            case DOWN:
                loc.add(0, -1, 0);
                break;
            case SOUTH:
                loc.add(0, 0, 1);
                break;
            case WEST:
                loc.add(-1, 0, 0);
                break;
            case NORTH:
                loc.add(0, 0, -1);
                break;
            case EAST:
                loc.add(1, 0, 0);
                break;
        }
        return loc;
    }
}
