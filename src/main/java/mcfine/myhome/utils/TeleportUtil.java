package mcfine.myhome.utils;

import mcfine.myhome.MyHome;
import mcfine.myhome.config.Locale;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeleportUtil {

    public static final HashMap<Player, ArrayList<BukkitTask>> playerTasks = new HashMap<>();
    private static final HashMap<Player, Long> playerOnTimer = new HashMap<>();
    private static final HashMap<Player, BukkitTask> cooldownTasks = new HashMap<>();
    private static final long refreshRate = 20;
    private static BukkitTask cooldownTask = null;


    public static int attemptTeleport(@NotNull Player p, @NotNull Location loc, boolean force, double cost, @Nullable Integer particleId, @Nullable String srv) {

        if (particleId == null) particleId = MyHome.particleId;

        if (playerOnTimer.containsKey(p)) {
            return 3;
        }

        if (force) {
            if (cost != 0) {
                EconomyResponse resp = MyHome.getEcon().withdrawPlayer(p, MyHome.cost);
                if (!resp.transactionSuccess()) return 2;
            }
            myHomeTeleport(p, loc, MyHome.warmup, MyHome.cooldown, particleId, cost);
            return 0;
        }
        if (!ifLocationSafe(loc).equals(SafetyType.SAFE)) {
            loc = findClosestSafeLocation(loc);
            if (loc == null) return 1;
        }
        if (cost != 0 && MyHome.isEconomyEnabled()) {
            EconomyResponse resp = MyHome.getEcon().withdrawPlayer(p, MyHome.cost);
            if (!resp.transactionSuccess()) return 2;
        }
        myHomeTeleport(p, loc, MyHome.warmup, MyHome.cooldown, particleId, cost);
        return 0;
    }

    public static void myHomeTeleport(Player p, Location loc, long warmup, long cooldown, int particleId, double cost) {
        if (warmup != 0) delayedTeleport(p, loc, warmup, particleId);
        else instantTeleport(p, loc, particleId);

        if (cooldown == 0) return;
        if (playerOnTimer.containsKey(p)) {
            System.out.println("Error: teleportation executed while player is still on cooldown. Report bug.");
            playerOnTimer.remove(p);
        }
        playerOnTimer.put(p, cooldown);
        if (cooldownTask == null) initCooldownTask();
    }

    private static void instantTeleport(Player p, Location loc, int particleId) {
        if(particleId == 0) {
            ParticleUtil.playParticles(p.getLocation(), 0);
            p.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            ParticleUtil.playParticles(loc, 0);
        } else if(particleId == -1){
            p.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    private static void delayedTeleport(Player p, Location loc, long delay, int particleId) {

        Location playerLocation = p.getLocation();
        ArrayList<BukkitTask> tasks = new ArrayList<>();

        if (particleId == -1) {
            instantTeleport(p, loc, -1);
        } else if (particleId == 0) {
            ParticleUtil.putPhase(p);
            BukkitTask particleEngine = new BukkitRunnable() {
                @Override
                public void run() {

                    ParticleUtil.playParticles(playerLocation, 0, ParticleUtil.getPhase(p));
                    ParticleUtil.incrementPhase(p, 1.0);

                    ParticleUtil.playParticles(playerLocation, 0, ParticleUtil.getPhase(p));
                    ParticleUtil.incrementPhase(p, 1.0);

                }
            }.runTaskTimer(MyHome.plugin, 0L, 1L);

            BukkitTask particleDisable = new BukkitRunnable() {
                @Override
                public void run() {
                    particleEngine.cancel();
                    ParticleUtil.removePhase(p);
                }
            }.runTaskLater(MyHome.plugin, delay);

            tasks.add(particleEngine);
            tasks.add(particleDisable);

            BukkitTask teleportTask = new BukkitRunnable() {
                @Override
                public void run() {
                    p.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    p.sendMessage(Locale.getString("home-tp-success") + Locale.getString("withdraw").replace("%cost%", MyHome.getEcon().format(MyHome.cost)));
                    ParticleUtil.playParticles(loc, particleId);
                    ParticleUtil.removePhase(p);
                    playerTasks.remove(p);
                }
            }.runTaskLater(MyHome.plugin, delay);

            tasks.add(teleportTask);
            playerTasks.put(p, tasks);
        } else if (particleId == 1) {
            boolean res = ParticleUtil.createPortalEffect(p, loc);
            if(!res) p.sendMessage(Locale.getString("no-space-for-portal"));
        }
    }


    public static SafetyType ifLocationSafe(Location loc) {
        try {
            Block feetBlock = loc.getBlock();
            Block headBlock;
            Block groundBlock;

            if (loc.getBlockY() + 1 > loc.getWorld().getMaxHeight()) headBlock = feetBlock;
            else headBlock = loc.add(0, 1, 0).getBlock();

            if (loc.getBlockY() - 1 < loc.getWorld().getMinHeight()) groundBlock = feetBlock;
            else groundBlock = loc.add(0, -1, 0).getBlock();

            if (headBlock.getType().isSolid()) {
                return SafetyType.SUFFOCATION; // will suffocate
            }

            if (headBlock.getType().equals(Material.LAVA) && feetBlock.getType().equals(Material.LAVA) && groundBlock.getType().equals(Material.LAVA)) {
                return SafetyType.LAVA;
            }

            if (!groundBlock.getType().isSolid() && !groundBlock.getType().equals(Material.WATER)) {
                Material ground_1 = groundBlock.getLocation().add(0, -1, 0).getBlock().getType();
                if (ground_1.equals(Material.LAVA)) return SafetyType.LAVA;
                if (!ground_1.isSolid() && !ground_1.equals(Material.WATER)) {
                    Material ground_2 = groundBlock.getLocation().add(0, -2, 0).getBlock().getType();
                    if (ground_2.equals(Material.LAVA)) return SafetyType.LAVA;
                    if (!ground_2.isSolid() && !ground_2.equals(Material.WATER)) {
                        return SafetyType.N0_FLOOR;
                    }
                }
            }

            return SafetyType.SAFE;

        } catch (Exception e) {
            System.out.println("Exception while checking for TP safety: " + e);
        }
        return SafetyType.ERROR;
    }

    public static Location findClosestSafeLocation(Location loc) {
        if (ifLocationSafe(loc).equals(SafetyType.SAFE)) return loc;
        for (Location location : generateSurroundings(loc, MyHome.checkRadius, false)) {
            if (ifLocationSafe(location).equals(SafetyType.SAFE)) return location;
        }
        return null;
    }

    public static List<Location> generateSurroundings(Location centerBlock, int radius, boolean hollow) {
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

                        circleBlocks.add(l);

                    }

                }
            }
        }

        return circleBlocks;
    }

    public static int getSecondsRemaining(Player p) {
        Long ticks = playerOnTimer.get(p);
        if (ticks == null) return 0;
        int seconds = (int) Math.round(((double) ticks) / 20);
        if (seconds == 0) seconds = 1;
        return seconds;
    }

    public static void clearMaps() {
        playerTasks.clear();
        playerOnTimer.clear();
        cooldownTasks.clear();
    }

    public static void clearTasks() {
        for (BukkitTask task : cooldownTasks.values()) {
            try {
                task.cancel();
            } catch (Exception ignored) {
            }

        }
        for (ArrayList<BukkitTask> taskList : playerTasks.values()) {
            for (BukkitTask task : taskList) {
                try {
                    task.cancel();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void initCooldownTask() {
        cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (playerOnTimer.size() == 0) {
                    this.cancel();
                    cooldownTask = null;
                }
                for (Map.Entry<Player, Long> entry : playerOnTimer.entrySet()) {
                    if (entry.getValue() < refreshRate) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                playerOnTimer.remove(entry.getKey());
                            }
                        }.runTaskLaterAsynchronously(MyHome.plugin, entry.getValue());

                    } else if (entry.getValue() == refreshRate) {
                        playerOnTimer.remove(entry.getKey());
                    } else playerOnTimer.put(entry.getKey(), entry.getValue() - refreshRate);
                }
            }
        }.runTaskTimerAsynchronously(MyHome.plugin, refreshRate, refreshRate);
    }

    public static void deleteCooldown(Player p) {
        playerOnTimer.remove(p);
    }


    public enum SafetyType {
        SUFFOCATION,
        LAVA,
        N0_FLOOR,
        SAFE,
        ERROR
    }

}
