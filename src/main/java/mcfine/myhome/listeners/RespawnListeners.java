package mcfine.myhome.listeners;

import mcfine.myhome.config.Locale;
import mcfine.myhome.utils.Home;
import mcfine.myhome.utils.HomeStorage;
import mcfine.myhome.utils.TeleportUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.HashSet;

public class RespawnListeners implements Listener {

    private static final HashSet<Material> beds = new HashSet<>() {
        {
            add(Material.BLACK_BED);
            add(Material.BLUE_BED);
            add(Material.BROWN_BED);
            add(Material.GREEN_BED);
            add(Material.CYAN_BED);
            add(Material.GRAY_BED);
            add(Material.LIGHT_BLUE_BED);
            add(Material.LIGHT_GRAY_BED);
            add(Material.LIME_BED);
            add(Material.MAGENTA_BED);
            add(Material.ORANGE_BED);
            add(Material.PINK_BED);
            add(Material.PURPLE_BED);
            add(Material.RED_BED);
            add(Material.WHITE_BED);
            add(Material.YELLOW_BED);
        }
    };

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {

        Location loc = event.getBed().getLocation();
        Player p = event.getPlayer();

        Home home = HomeStorage.getHome(p.getName().toLowerCase());
        if (home == null) {
            HomeStorage.putHome(new Home(loc.getX(), loc.getY(), loc.getZ(),
                    p.getLocation().getYaw(), 0.0F, "default", p.getName(),
                    new ArrayList<>(), loc.getWorld().getName(), false));
            p.sendMessage(Locale.getString("home-set"));
        }
    }


    //
    @EventHandler
    public void onInteract(PlayerInteractEvent ev) {
        if (ev.getAction().isRightClick() && ev.getClickedBlock() != null && beds.contains(ev.getClickedBlock().getType())) {
            if (ev.getPlayer().getWorld().isDayTime()) {
                ev.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent ev) {
        ev.setSpawnLocation(false);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRespawn(PlayerRespawnEvent ev) {
        Player p = ev.getPlayer();
        Home home = HomeStorage.getHome(p.getName());

        if (home == null) {
            p.sendMessage(Locale.getString("no-respawn-home"));
            return;
        }
        if (TeleportUtil.ifLocationSafe(home.getLocation()).equals(TeleportUtil.SafetyType.SAFE)) {
            p.sendMessage(Locale.getString("respawned"));
            p.teleportAsync(home.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        } else {
            Location safeLocation = TeleportUtil.findClosestSafeLocation(home.getLocation());
            if (safeLocation == null) {
                p.sendMessage(Locale.getString("not-safe-respawn"));
            } else {
                p.sendMessage(Locale.getString("respawned"));
                p.teleportAsync(home.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
    }

}
