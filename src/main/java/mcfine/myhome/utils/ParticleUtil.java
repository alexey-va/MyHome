package mcfine.myhome.utils;

import mcfine.myhome.MyHome;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.DustColorTransitionData;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ParticleUtil {

    private static final HashMap<Player, Double> playerPhase = new HashMap<>();
    public static HashMap<Player, Integer> portalPhases = new HashMap<>();
    public static HashSet<Location> portalLocations = new HashSet<>();
    private static final HashMap<Player, ArrayList<Location>> playerPortalLocations = new HashMap<>();
    private static final HashMap<Player, BukkitTask> portalCreationTasks = new HashMap<>();

    public static Location createCircle(double radius, double angleRad, double x, double y, double z, World world) {
        Location result = new Location(world, x, y, z);
        result.add(radius * Math.cos(angleRad), 0, radius * Math.sin(angleRad));
        return result;
    }

    public static void playParticles(Location loc, int particleId) {
        ParticleEffect effect;
        try {
            effect = ParticleEffect.valueOf(MyHome.particleEffect);
        } catch (Exception ex) {
            effect = ParticleEffect.FLAME;
        }

        Location centerLocation = loc.toCenterLocation();

        new ParticleBuilder(effect, centerLocation)
                .setOffset(1f, 1f, 1f)
                .setAmount(100)
                .setSpeed(0f)
                .display();
    }

    public static void playParticles(Location loc, int particleId, double phase) {
        ParticleEffect effect;
        try {
            effect = ParticleEffect.valueOf(MyHome.particleEffect);
        } catch (Exception ex) {
            effect = ParticleEffect.FLAME;
        }

        Location centerLocation = new Location(loc.getWorld(), loc.getX(), loc.getY() + Math.sin(phase / 20.252 - 1.5708) + 1, loc.getZ());

        new ParticleBuilder(effect, createCircle(0.8 + Math.sin(phase / 20.252) * 0.2, phase / 2.341, centerLocation.getX(),
                centerLocation.getY(), centerLocation.getZ(), centerLocation.getWorld()))
                .setOffset(0.01f, 0.0f, 0.01f)
                .setAmount(10)
                .setSpeed(0f)
                .display();
    }

    public static boolean createPortalEffect(Player p, Location targetLocation) {
        Location location = findPortalLocation(p);
        if (location == null) {
            return false;
        }

        if (portalCreationTasks.containsKey(p)) {
            Location location_old = playerPortalLocations.get(p).get(0);

            location_old.getBlock().setType(Material.AIR);
            portalLocations.remove(location_old);

            location_old.add(0, 1, 0).getBlock().setType(Material.AIR);
            portalLocations.remove(location_old);

            portalCreationTasks.get(p).cancel();
            portalCreationTasks.remove(p);
        }

        portalPhases.put(p, 1);
        playerPortalLocations.put(p, new ArrayList<>(List.of(location)));

        portalLocations.add(location);
        portalLocations.add(new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ()));

        portalCreationTasks.put(p, new BukkitRunnable() {
            @Override
            public void run() {
                Integer phase = portalPhases.get(p);
                if (phase == 200) {
                    removePortal(p, location);
                    this.cancel();
                    return;
                } else if (phase <= 40) {
                    buildOutliner(p, phase, location);
                } else if (phase == 41) {
                    createPortal(p, location);
                } else if (ifInPortal(p, location)) {
                    p.getWorld().playSound(p.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT, 15f, 1f);
                    p.teleportAsync(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY(),
                            targetLocation.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    removePortal(p, location);
                    this.cancel();
                }
                portalPhases.put(p, phase + 1);
                putParticles(p);
            }
        }.runTaskTimer(MyHome.plugin, 2L, 2L));
        return true;
    }


    private static boolean ifInPortal(Player p, Location location) {
        return (p.getLocation().toBlockLocation().getX() == location.toBlockLocation().getX() &&
                p.getLocation().toBlockLocation().getZ() == location.toBlockLocation().getZ() &&
                p.getLocation().getY() - location.getY() < 1.5 && p.getLocation().getY() - location.getY() > -1);
    }

    private static void removePortal(Player p, Location location) {
        location.getBlock().setType(Material.AIR);
        portalLocations.remove(location);

        location.add(0, 1, 0).getBlock().setType(Material.AIR);
        portalLocations.remove(location);
    }

    private static void createPortal(Player p, Location location) {
        location.getWorld().playSound(location, Sound.BLOCK_END_PORTAL_SPAWN, 1f, 1f);
        location.getBlock().setType(Material.END_GATEWAY);
        new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ()).getBlock().setType(Material.END_GATEWAY);
    }


    private static void buildOutliner(Player p, Integer phase, Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        World world = location.getWorld();
        if (phase <= 10) {
            if(phase == 1) location.getWorld().playSound(location,Sound.BLOCK_AMETHYST_BLOCK_CHIME, 15f, 1f);
            Location loc1 = new Location(world, x + phase / 10.0, y, z);
            Location loc2 = new Location(world, x, y + phase / 10.0, z);
            Location loc3 = new Location(world, x, y, z + phase / 10.0);
            playerPortalLocations.get(p).add(loc1);
            playerPortalLocations.get(p).add(loc2);
            playerPortalLocations.get(p).add(loc3);
        } else if (phase <= 20) {
            if(phase == 20) world.playSound(location,Sound.BLOCK_AMETHYST_BLOCK_CHIME, 15f, 1f);
            Location loc1 = new Location(world, x + 1, y + (phase - 10) / 10.0, z);
            Location loc2 = new Location(world, x, y + phase / 10.0, z);
            Location loc3 = new Location(world, x, y + (phase - 10) / 10.0, z + 1);
            Location loc4 = new Location(world, x + 1, y, z + (phase - 10) / 10.0);
            Location loc5 = new Location(world, x + (phase - 10) / 10.0, y, z + 1);
            playerPortalLocations.get(p).add(loc1);
            playerPortalLocations.get(p).add(loc2);
            playerPortalLocations.get(p).add(loc3);
            playerPortalLocations.get(p).add(loc4);
            playerPortalLocations.get(p).add(loc5);
        } else if (phase <= 30) {
            Location loc1 = new Location(world, x + 1, y + (phase - 10) / 10.0, z);
            Location loc2 = new Location(world, x + (phase - 20) / 10.0, y + 2, z);
            Location loc3 = new Location(world, x, y + 2, z + (phase - 20) / 10.0);
            Location loc4 = new Location(world, x, y + (phase - 10) / 10.0, z + 1);
            Location loc5 = new Location(world, x + 1, y + (phase - 20) / 10.0, z + 1);
            playerPortalLocations.get(p).add(loc1);
            playerPortalLocations.get(p).add(loc2);
            playerPortalLocations.get(p).add(loc3);
            playerPortalLocations.get(p).add(loc4);
            playerPortalLocations.get(p).add(loc5);
        } else if (phase <= 40) {
            Location loc1 = new Location(world, x + 1, y + 2, z + (phase - 30) / 10.0);
            Location loc2 = new Location(world, x + (phase - 30) / 10.0, y + 2, z + 1);
            Location loc3 = new Location(world, x + 1, y + 1 + (phase - 30) / 10.0, z + 1);
            playerPortalLocations.get(p).add(loc1);
            playerPortalLocations.get(p).add(loc2);
            playerPortalLocations.get(p).add(loc3);
        }
    }

    private static void putParticles(Player p){
        for (Location loc : playerPortalLocations.get(p)) {
            new ParticleBuilder(ParticleEffect.DUST_COLOR_TRANSITION, loc).setAmount(3).setOffset(0.015f, 0.015f, 0.015f)
                    .setParticleData(new DustColorTransitionData(new Color(91, 16, 123), new Color(0, 0, 0), 0.5f)).display();
        }
    }


    public static boolean createPortalEffect(Player p, String command) {
        Location location = findPortalLocation(p);
        if (location == null) {
            p.sendMessage("Null");
            return false;
        }

        if (portalCreationTasks.containsKey(p)) {
            Location location_old = playerPortalLocations.get(p).get(0);

            location_old.getBlock().setType(Material.AIR);
            portalLocations.remove(location_old);

            location_old.add(0, 1, 0).getBlock().setType(Material.AIR);
            portalLocations.remove(location_old);

            portalCreationTasks.get(p).cancel();
            portalCreationTasks.remove(p);
        }

        portalPhases.put(p, 1);
        playerPortalLocations.put(p, new ArrayList<>(List.of(location)));

        portalLocations.add(location);
        portalLocations.add(new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ()));

        portalCreationTasks.put(p, new BukkitRunnable() {
            @Override
            public void run() {
                Integer phase = portalPhases.get(p);
                if (phase == 200) {
                    this.cancel();
                    removePortal(p, location);
                    return;
                } else if (phase <= 40) {
                    buildOutliner(p, phase, location);
                } else if (phase == 41) {
                    createPortal(p, location);
                } else if (ifInPortal(p, location)) {
                    p.performCommand(command);
                    removePortal(p, location);
                    this.cancel();
                }
                portalPhases.put(p, phase + 1);
                putParticles(p);
            }
        }.runTaskTimer(MyHome.plugin, 2L, 2L));
        return true;
    }


    public static Location findPortalLocation(Player p) {
        Block targetBlock = p.getTargetBlockExact(6);
        if (targetBlock == null) {
            int direction = (int) Math.round(p.getLocation().getYaw() / 90.00);

            switch (direction) {
                case -1 -> targetBlock = p.getLocation().add(0, -0.35, 0).toBlockLocation().add(1, 0, 0).getBlock();
                case 0 -> targetBlock = p.getLocation().add(0, -0.35, 0).toBlockLocation().add(0, 0, 1).getBlock();
                case 1 -> targetBlock = p.getLocation().add(0, -0.35, 0).toBlockLocation().add(-1, 0, 0).getBlock();
                case 2, -2 -> targetBlock = p.getLocation().add(0, -0.35, 0).toBlockLocation().add(0, 0, -1).getBlock();
            }
        }

        if (targetBlock == null) {
            for (Location loc : generateSurroundings2(p.getLocation(), 5, false)) {
                if (isSuitable(loc)) return loc;
            }
            return null;
        }

        if (targetBlock.getType().equals(Material.SNOW))
            targetBlock = targetBlock.getLocation().add(0, -1, 0).getBlock();
        Location targetLocation = targetBlock.getLocation();

        if (isSuitable(targetLocation)) return targetLocation;
        else {
            for (Location loc : generateSurroundings2(targetLocation, 5, false)) {
                if (isSuitable(loc)) return loc;
            }
        }

        return null;

    }

    private static boolean isSuitable(Location loc) {
        if (loc == null) {
            return false;
        }

        boolean res = (loc.getBlock().isSolid() || loc.getBlock().getType().equals(Material.WATER)) &&
                ((loc.add(0, 1, 0).getBlock().isEmpty() || loc.getBlock().getType().equals(Material.SNOW)) && !portalLocations.contains(loc))
                && ((loc.add(0, 1, 0).getBlock().isEmpty() || loc.getBlock().getType().equals(Material.SNOW)) && !portalLocations.contains(loc));
        loc.add(0, -1, 0);
        return res;
    }

    public static List<Location> generateSurroundings2(Location centerBlock, int radius, boolean hollow) {
        if (centerBlock == null) {
            return new ArrayList<>();
        }

        List<Location> circleBlocks = new ArrayList<>();

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                for (int z = bz - radius; z <= bz + radius; z++) {

                    double distance = Math.abs(bx - x) + Math.abs(bz - z) + Math.abs(by - y);

                    if (distance <= radius && !(hollow && distance == radius)) {

                        Location l = new Location(centerBlock.getWorld(), x, y, z);
                        if ((l.getBlock().isCollidable() || l.getBlock().getType().equals(Material.WATER))) {
                            circleBlocks.add(l);
                        }
                    }
                }
            }
        }

        return circleBlocks;
    }

    public static double getPhase(Player p) {
        return playerPhase.get(p);
    }

    public static void removePhase(Player p) {
        playerPhase.remove(p);
    }

    public static void incrementPhase(Player p, double incr) {
        playerPhase.put(p, playerPhase.get(p) + incr);
    }

    public static void putPhase(Player p) {
        playerPhase.put(p, 0.0);
    }
}
