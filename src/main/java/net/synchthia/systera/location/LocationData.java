package net.synchthia.systera.location;

import lombok.Data;
import lombok.NonNull;

import java.util.Optional;

/**
 * @author Laica-Lunasys
 */
@Data
public class LocationData {
    @NonNull
    private String worldName;

    private boolean whenLogin;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public LocationData(String worldName, boolean whenLogin, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.whenLogin = whenLogin;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
