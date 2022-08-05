package mcfine.myhome.tasks;

import mcfine.myhome.MyHome;
import mcfine.myhome.utils.Home;
import mcfine.myhome.utils.HomeStorage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.ArrayList;

public class AutosaveTask {

    private final BukkitTask saveTask;
    private final BukkitTask refreshTimeTask;

    public AutosaveTask(Plugin plugin) {

        saveTask = new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    HomeStorage.saveData(false);
                } catch (IOException e) {
                    MyHome.plugin.warn("Error in async saving: " + e);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 12000L, 12000L);

        refreshTimeTask = new BukkitRunnable() {
            @Override
            public void run() {
                ArrayList<Player> playerList = new ArrayList<>(plugin.getServer().getOnlinePlayers());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player p : playerList) {
                            Home home = HomeStorage.getHome(p.getName().toLowerCase());
                            if (home != null) home.updateDate();
                        }
                    }
                }.runTaskAsynchronously(plugin);
            }
        }.runTaskTimer(plugin, 100L, 100L);

    }

    public void cancel() {
        this.refreshTimeTask.cancel();
        this.saveTask.cancel();
    }

}
