package net.synchthia.systera.chair;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Stairs;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.*;

/**
 * @author Laica-Lunasys
 */
public class ChairListener implements Listener {
    private final SysteraPlugin plugin;

    public ChairListener(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    private Map<Player, ChairData> chairs = new HashMap<>();
    private final List<Material> chairsBlock = new ArrayList<Material>() {
        {
            add(Material.ACACIA_STAIRS);
            add(Material.BIRCH_WOOD_STAIRS);
            add(Material.JUNGLE_WOOD_STAIRS);
            add(Material.SPRUCE_WOOD_STAIRS);
            add(Material.WOOD_STAIRS);
        }
    };

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location playerLoc = player.getLocation();
        Block block = event.getClickedBlock();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (player.isSneaking()) {
            return;
        }

        if (chairsBlock.contains(block.getType())) {
            event.setCancelled(true);

            Location loc = block.getLocation();
            Stairs stairs = (Stairs) block.getState().getData();

            // Is Top?
            if (stairs.isInverted()) {
                return;
            }

            // Already Sitting?
            if (chairs.containsKey(player)) {
                return;
            }

            Optional<ChairData> first = chairs.values().stream().filter(v -> v.getArmorStand().getLocation() == loc).findFirst();
            if (first.isPresent()) {
                return;
            }

            switch (stairs.getFacing()) {
                case SOUTH:
                    loc.add(0.5, 0.3, 0.7);
                    break;
                case WEST:
                    loc.add(0.3, 0.3, 0.5);
                    break;
                case NORTH:
                    loc.add(0.5, 0.3, 0.3);
                    break;
                case EAST:
                    loc.add(0.7, 0.3, 0.5);
                    break;
            }

            ArmorStand armorStand = block.getWorld().spawn(loc, ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.getPassengers().forEach(armorStand::removePassenger);
            armorStand.setSmall(true);
            armorStand.setGravity(false);
            armorStand.setCollidable(false);
            armorStand.setInvulnerable(true);
            armorStand.setBasePlate(false);
            armorStand.setMarker(true);
            armorStand.addPassenger(player);

            chairs.put(player, new ChairData(armorStand, playerLoc));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (chairs.containsKey(player)) {
            ChairData chair = chairs.get(player);
            chair.getArmorStand().remove();
            player.teleport(chair.getBeforeLocation());
            chairs.remove(player);
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = Bukkit.getPlayer(event.getEntity().getUniqueId());
            if (chairs.containsKey(player)) {
                ChairData chair = chairs.get(player);
                chair.getArmorStand().remove();

                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                                player.teleport(chair.getBeforeLocation()),
                        1L);

                chairs.remove(player);
            }
        }
    }
}
