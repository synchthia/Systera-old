package net.synchthia.systera.gate;

import net.synchthia.systera.SysteraPlugin;
import net.synchthia.systera.util.BlockUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Sign;

import java.util.Optional;

/**
 * @author Laica-Lunasys
 */
public class GateListener implements Listener {
    private final SysteraPlugin plugin;

    public GateListener(SysteraPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreateSign(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("systera.gate.manage")) {
            return;
        }

        // Sign Content
        String name = event.getLine(0);
        String destination = event.getLine(1);
        World world = event.getBlock().getWorld();

        // Sign Location / Child Block
        Sign sign = (Sign) event.getBlock().getState().getData();
        Block attended = event.getBlock().getRelative(sign.getAttachedFace());

        // Check Layout
        // (G=Glow Stone, X=IRON Block, o=AIR, s=WallSign, p=portal(AIR))
        // [A4~] ANY
        // [A3] o
        // [A2] p
        // [A1] G
        // [A0] Xs [X=attended]

        // A3
        Location a3 = new Location(world, attended.getX(), attended.getY() + 3, attended.getZ());
        if (!world.getBlockAt(a3).getType().equals(Material.AIR) &&
                !world.getBlockAt(a3).getType().equals(Material.WATER) &&
                !world.getBlockAt(a3).getType().equals(Material.STATIONARY_WATER)) return;

        // A2
        Location a2 = new Location(world, attended.getX(), attended.getY() + 2, attended.getZ());
        if (!world.getBlockAt(a2).getType().equals(Material.AIR) &&
                !world.getBlockAt(a2).getType().equals(Material.WATER) &&
                !world.getBlockAt(a2).getType().equals(Material.STATIONARY_WATER)) return;
        // A1
        Location a1 = new Location(world, attended.getX(), attended.getY() + 1, attended.getZ());
        if (!world.getBlockAt(a1).getType().equals(Material.GLOWSTONE)) return;

        // A0
        Location a0 = attended.getLocation();
        if (!world.getBlockAt(a0).getType().equals(Material.IRON_BLOCK)) return;

        // Check Sign
        if (name.isEmpty() || destination.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Name/Destination is Empty!");
            return;
        }

        // Range
        // Gate [A0] ~ [A3] (Full)
        GateData.Range gateRange = new GateData.Range(a0, a3);

        // Portal [A2] ~ [A3]
        GateData.Range portalRange = new GateData.Range(a2, a3);

        GateData data = new GateData(name, destination, event.getBlock().getLocation(), gateRange, portalRange);

        boolean result = plugin.gateManager.add(data);
        if (result) {
            fillGate(data, Material.STATIONARY_WATER);
            player.sendMessage(String.format(ChatColor.GREEN + "Gate Created! %s -> %s", data.getName(), data.getDestination()));
        } else {
            player.sendMessage(ChatColor.RED + "Already Exist");
        }
    }

    @EventHandler
    public void onPhysicWater(BlockPhysicsEvent event) {
        GateManager gateManager = plugin.gateManager;

        Block block = event.getBlock();
        Location loc = block.getLocation();

        // Is Liquid?
        if (!block.getType().equals(Material.WATER) &&
                !block.getType().equals(Material.STATIONARY_WATER) &&
                !block.getType().equals(Material.LAVA) &&
                !block.getType().equals(Material.STATIONARY_LAVA)) {
            return;
        }

        if (gateManager.inGate(loc).isPresent()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        GateManager gateManager = plugin.gateManager;
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        Optional<GateData> opt = gateManager.inPortal(event.getClickedBlock().getLocation());
        if (opt.isPresent()) {
            event.setCancelled(true);
            return;
        }

        Optional<GateData> opt2 = gateManager.inPortal(BlockUtil.getClickedLocation(event.getClickedBlock(), event.getBlockFace()));
        if (opt2.isPresent()) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        GateManager gateManager = plugin.gateManager;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();

        Optional<GateData> optPortal = gateManager.inPortal(loc);
        if (optPortal.isPresent()) {
            event.setCancelled(true);
            return;
        }

        GateData gate;
        if (block.getType().equals(Material.WALL_SIGN)) {
            Optional<GateData> opt = gateManager.inSign(block);
            if (!opt.isPresent()) {
                return;
            }

            gate = opt.get();
        } else {
            Optional<GateData> opt = gateManager.inGate(loc);
            if (!opt.isPresent()) {
                return;
            }
            gate = opt.get();
        }

        if (!player.hasPermission("systera.gate.manage")) {
            event.setCancelled(true);
            return;
        }

        if (gateManager.remove(gate.getName())) {
            fillGate(gate, Material.AIR);
            player.sendMessage(ChatColor.GREEN + "Gate Removed: " + gate.getName());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        GateManager gateManager = plugin.gateManager;
        Player player = event.getPlayer();
        Location loc = event.getTo();

        Optional<GateData> optPortal = gateManager.inPortal(loc);

        if (optPortal.isPresent()) {
            GateData gate = optPortal.get();
            GateData target = gateManager.findGate(gate.getDestination());
            if (target == null) {
                return;
            }

            Location to = calcTeleportPoint(loc, gate, target);
            player.teleport(to);
        }
    }

    private Location calcTeleportPoint(Location loc, GateData origin, GateData dest) {
        GateData.Range originPortal = origin.getPortalRange();
        GateData.Range destPortal = dest.getPortalRange();

        Location orgSignLoc = new Location(Bukkit.getWorld(origin.getWorldID()), origin.getSignX(), origin.getSignY(), origin.getSignZ());
        BlockFace orgFace = ((Sign) orgSignLoc.getBlock().getState().getData()).getFacing();

        Location destSignLoc = new Location(Bukkit.getWorld(dest.getWorldID()), dest.getSignX(), dest.getSignY(), dest.getSignZ());
        BlockFace destFace = ((Sign) destSignLoc.getBlock().getState().getData()).getFacing();


        Location target = new Location(
                Bukkit.getWorld(dest.getWorldID()),
                destPortal.getMin()[0],
                destPortal.getMin()[1],
                destPortal.getMin()[2]);

        // ORG
        double[] orgMin = originPortal.getMin();
        double[] orgMax = originPortal.getMax();

        // DEST
        double[] destMin = destPortal.getMin();
        double[] destMax = destPortal.getMax();

        // Generate OFFSET FROM ORIGIN GATE
        double offset = 0.5;
//        switch (orgFace) {
//            case SOUTH:
//                offset = minusOrDefault(loc.getX(), orgMin[0]);
//                break;
//            case WEST:
//                offset = minusOrDefault(loc.getZ(), orgMin[2]);
//                break;
//            case NORTH:
//                offset = minusOrDefault(orgMin[0], loc.getX());
//                break;
//            case EAST:
//                offset = minusOrDefault(orgMin[2], loc.getZ());
//                break;
//        }

        // Generate Teleport Point
        switch (destFace) {
            case SOUTH:
                target = target.add(offset, minusOrDefault(orgMax[1], (loc.getY() + 1)), 1.5);
                break;
            case WEST:
                target = target.add(-0.5, minusOrDefault(orgMax[1], (loc.getY() + 1)), offset);
                break;
            case NORTH:
                target = target.add(offset, minusOrDefault(orgMax[1], (loc.getY() + 1)), -0.5);
                break;
            case EAST:
                target = target.add(1.5, minusOrDefault(orgMax[1], (loc.getY() + 1)), offset);
                break;
        }

        target.setYaw(convertBlockFace(destFace));
        target.setPitch(loc.getPitch());

        return target;
    }

    private void fillGate(GateData gate, Material material) {
        double[] min = gate.getPortalRange().getMin();
        double[] max = gate.getPortalRange().getMax();
        for (double i = min[0]; i <= max[0]; i++) {
            for (double j = min[1]; j <= max[1]; j++) {
                for (double k = min[2]; k <= max[2]; k++) {
                    Location l = new Location(Bukkit.getWorld(gate.getWorldID()), i, j, k);
                    l.getBlock().setType(material, false);
                }
            }
        }
    }

    private float convertBlockFace(BlockFace blockFace) {
        switch (blockFace) {
            case SOUTH:
                return 0;
            case WEST:
                return 90;
            case NORTH:
                return 180;
            case EAST:
                return -90;
        }
        return 0;
    }

    private double minusOrDefault(double a, double b) {
        if (a > b) {
            return a - b;
        } else {
            return b - a;
        }
    }
}
