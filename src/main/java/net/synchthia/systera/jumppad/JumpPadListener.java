package net.synchthia.systera.jumppad;

import net.synchthia.systera.SysteraPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Laica-Lunasys
 */
public class JumpPadListener implements Listener {
    private final SysteraPlugin plugin;

    public JumpPadListener(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    private final List<Material> baseBlock = new ArrayList<Material>() {
        {
            add(Material.SLIME_BLOCK);
            add(Material.REDSTONE_LAMP_OFF);
            add(Material.REDSTONE_LAMP_ON);
        }
    };

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo().getY() % 1 != 0 || (event.getFrom().getY() % 1 == 0 && event.getFrom().getBlock().equals(event.getTo().getBlock()))) {
            return;
        }

        Block relative = event.getTo().getBlock().getRelative(BlockFace.DOWN);
        if (!baseBlock.contains(relative.getType())) {
            return;
        }

        switch (event.getTo().getBlock().getType()) {
            case GOLD_PLATE:
                shoot(event.getPlayer(), 1, 2);
                break;
            case IRON_PLATE:
                shoot(event.getPlayer(), 1, 1.5);
                break;
            case STONE_PLATE:
                shoot(event.getPlayer(), 0.5, 1);
                break;
            case WOOD_PLATE:
                shoot(event.getPlayer(), 0.5, 0.5);
                break;
        }

    }

    private void shoot(Player player, double height, double length) {
        Sound sound = Sound.ENTITY_ZOMBIE_INFECT;
        float soundPitch = 1;

        player.playSound(player.getLocation(), sound, 1.0F, soundPitch);

        player.setVelocity(player.getLocation().getDirection().normalize().multiply(length).setY(height));
        Bukkit.getServer().getScheduler().runTaskLater(plugin, ()-> {
            player.setVelocity(player.getLocation().getDirection().normalize().multiply(length*2).setY(height));
            Bukkit.getServer().getScheduler().runTaskLater(plugin, ()-> {
                player.setVelocity(player.getLocation().getDirection().normalize().multiply(length*3).setY(height));
                Bukkit.getServer().getScheduler().runTaskLater(plugin, ()-> {
                    player.setVelocity(player.getLocation().getDirection().normalize().multiply(length*4).setY(height));
                }, 2L);
            }, 2L);
        }, 2L);
    }
}
