package mcfine.myhome.listeners;

import mcfine.myhome.utils.ParticleUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PortalListener implements Listener {


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPlaceBlock(BlockPlaceEvent ev) {
        if (ParticleUtil.portalLocations.contains(ev.getBlock().getLocation())) {
            ev.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent ev) {

        String[] args = ev.getMessage().split(" ");

        if (args.length == 3 && args[0].equalsIgnoreCase("/cmi") && args[1].equalsIgnoreCase("warp")) {
            ParticleUtil.createPortalEffect(ev.getPlayer(), ev.getMessage().substring(1));
            ev.setCancelled(true);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("/cmi") && args[1].equalsIgnoreCase("spawn")) {
            ParticleUtil.createPortalEffect(ev.getPlayer(), ev.getMessage().substring(1));
            ev.setCancelled(true);
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("/pw") || args[0].equalsIgnoreCase("/warp"))) {
            if (args[1].equalsIgnoreCase("about") ||
                    args[1].equalsIgnoreCase("addwarps") ||
                    args[1].equalsIgnoreCase("amount") ||
                    args[1].equalsIgnoreCase("ban") ||
                    args[1].equalsIgnoreCase("category") ||
                    args[1].equalsIgnoreCase("desc") ||
                    args[1].equalsIgnoreCase("favourite") ||
                    args[1].equalsIgnoreCase("icon") ||
                    args[1].equalsIgnoreCase("list") ||
                    args[1].equalsIgnoreCase("open") ||
                    args[1].equalsIgnoreCase("rate") ||
                    args[1].equalsIgnoreCase("reload") ||
                    args[1].equalsIgnoreCase("remove") ||
                    args[1].equalsIgnoreCase("removeall") ||
                    args[1].equalsIgnoreCase("rename") ||
                    args[1].equalsIgnoreCase("reset") ||
                    args[1].equalsIgnoreCase("set") ||
                    args[1].equalsIgnoreCase("setowner")
            ) {
            } else {
                ParticleUtil.createPortalEffect(ev.getPlayer(), ev.getMessage().substring(1));
                ev.setCancelled(true);
            }
        }
    }


}
