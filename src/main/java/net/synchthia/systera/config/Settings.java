package net.synchthia.systera.config;

import lombok.Data;
import net.synchthia.systera.util.Position;

import java.util.Optional;

/**
 * @author Nekoneko
 */

@Data
public class Settings {
    private Position spawnPoint;

    public Optional<Position> getSpawnPoint() {
        return Optional.ofNullable(spawnPoint);
    }
}
