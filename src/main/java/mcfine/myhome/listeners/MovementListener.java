package mcfine.myhome.listeners;

import mcfine.myhome.MyHome;
import mcfine.myhome.config.Locale;
import mcfine.myhome.utils.TeleportUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

public class MovementListener implements Listener {

    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent ev) {
        if (ev.hasChangedBlock() && MyHome.cancelOnMovement && TeleportUtil.playerTasks.containsKey(ev.getPlayer())) {
            Player p = ev.getPlayer();
            for (BukkitTask task : TeleportUtil.playerTasks.get(p)) {
                task.cancel();
                TeleportUtil.deleteCooldown(p);
            }
            TeleportUtil.playerTasks.remove(p);
            p.sendMessage(Locale.getString("cancel-on-movement"));
        }
    }

}
