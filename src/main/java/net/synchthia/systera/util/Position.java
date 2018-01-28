package net.synchthia.systera.util;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Optional;

/**
 * @author Nekoneko
 */
@Data
public class Position implements Cloneable {
    private final String world;
    private double x, y, z;
    private float yaw;
    private float pitch;

    private Position(Position pos) {
        this(pos.world, pos.x, pos.y, pos.z, pos.yaw, pos.pitch);
    }

    public Position(Location location) {
        this(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public Position(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getWorldName() {
        return world;
    }

    public Optional<World> getWorld() {
        return Optional.ofNullable(Bukkit.getWorld(world));
    }

    public Optional<Location> toBukkitLocation() {
        return getWorld().map(world -> new Location(world, x, y, z, yaw, pitch));
    }

    public Position add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Block) {
            Block block = (Block) object;
            return block.getX() == this.x && block.getY() == this.y && block.getZ() == this.z && block.getWorld().getName().equals(this.world);
        }
        return super.equals(object);
    }

    @Override
    public Position clone() {
        return new Position(this);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
