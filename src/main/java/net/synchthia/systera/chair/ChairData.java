package net.synchthia.systera.chair;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

/**
 * @author Laica-Lunasys
 */
@Data
public class ChairData {
    @NonNull
    private ArmorStand armorStand;
    private final Location beforeLocation;
}