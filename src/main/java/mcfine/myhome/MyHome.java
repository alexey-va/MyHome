package mcfine.myhome;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import mcfine.myhome.commands.DelHomeCommand;
import mcfine.myhome.commands.HomeCommand;
import mcfine.myhome.commands.HomeOfCommand;
import mcfine.myhome.commands.SetHomeCommand;
import mcfine.myhome.config.CustomConfig;
import mcfine.myhome.listeners.MovementListener;
import mcfine.myhome.listeners.PortalListener;
import mcfine.myhome.listeners.RespawnListeners;
import mcfine.myhome.tabcomplete.DelHomeTabcompleter;
import mcfine.myhome.tabcomplete.HomeOfTabcompleter;
import mcfine.myhome.tabcomplete.HomeTabcompleter;
import mcfine.myhome.tabcomplete.SetHomeTabcompleter;
import mcfine.myhome.tasks.AutosaveTask;
import mcfine.myhome.utils.HomeStorage;
import mcfine.myhome.utils.TeleportUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MyHome extends JavaPlugin implements Listener {

    public static MyHome plugin;
    public static String locale = "ru-ru";

    public static long warmup = 0;
    public static long cooldown = 0;
    public static int particleId = 0;
    public static double cost = 0.0;
    public static boolean cancelOnMovement = false;
    public static int checkRadius = 5;
    public static String particleEffect = "FLAME";
    private static ProtocolManager manager;
    private static Economy econ = null;
    private static boolean economyEnabled = false;
    private AutosaveTask autosave = null;
    private HomeStorage homeStorage;

    public static Economy getEcon() {
        return econ;
    }

    public static boolean isEconomyEnabled() {
        return economyEnabled;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        manager = ProtocolLibrary.getProtocolManager();

        init();


    }

    @Override
    public void onDisable() {
        try {
            HomeStorage.saveData(false);
            if (HomeStorage.getMysql() != null && !HomeStorage.getMysql().getConnection().isClosed()) {
                HomeStorage.getMysql().getConnection().close();
            }
        } catch (Exception ex) {
            warn("Error saving: " + ex);
        }
    }

    public void init() {
        if (autosave != null) {
            try {
                autosave.cancel();
            } catch (Exception ignored) {
            }
        }

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        CustomConfig.setup();

        homeStorage = new HomeStorage();

        HomeStorage.loadData(true);

        particleEffect = getConfig().getString("particle-effect", "FLAME").trim().toUpperCase();
        checkRadius = getConfig().getInt("check-radius", 5);
        locale = getConfig().getString("language", "ru-ru");
        warmup = getConfig().getLong("warmup", 60);
        cooldown = getConfig().getLong("cooldown", 1200);
        particleId = getConfig().getInt("particle-id", 0);
        cost = getConfig().getDouble("cost", 0);
        cancelOnMovement = getConfig().getBoolean("cancel-on-movement", true);

        getServer().getPluginManager().registerEvents(new RespawnListeners(), this);
        getServer().getPluginManager().registerEvents(new MovementListener(), this);
        getServer().getPluginManager().registerEvents(new PortalListener(), this);

        registerCommands();

        if (getServer().getPluginManager().getPlugin("ProtocolLib") == null)
            warn("No ProtocolLib. SOme features are disabled.");
        else registerProtocolListener();

        if (!setupEconomy()) {
            System.out.printf("[%s] - Disabled due to no Vault dependency found!%n", getDescription().getName());
            //getServer().getPluginManager().disablePlugin(this);
            return;
        } else economyEnabled = true;

        autosave = new AutosaveTask(plugin);

        TeleportUtil.clearTasks();
        TeleportUtil.clearMaps();
    }

    private void registerCommands() {
        try {
            Objects.requireNonNull(getCommand("home")).setExecutor(new HomeCommand());
            Objects.requireNonNull(getCommand("home")).setTabCompleter(new HomeTabcompleter());

            Objects.requireNonNull(getCommand("sethome")).setExecutor(new SetHomeCommand());
            Objects.requireNonNull(getCommand("sethome")).setTabCompleter(new SetHomeTabcompleter());

            Objects.requireNonNull(getCommand("delhome")).setExecutor(new DelHomeCommand());
            Objects.requireNonNull(getCommand("delhome")).setTabCompleter(new DelHomeTabcompleter());

            Objects.requireNonNull(getCommand("home-of")).setExecutor(new HomeOfCommand());
            Objects.requireNonNull(getCommand("home-of")).setTabCompleter(new HomeOfTabcompleter());
        } catch (Exception ex) {
            warn("Error registering commands: " + ex);
            ex.printStackTrace();
        }
    }

    private void registerProtocolListener() {
        try {
            manager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGH, PacketType.Play.Server.GAME_STATE_CHANGE) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    if (event.getPacket().getGameStateIDs().read(0) == 0) event.setCancelled(true);
                }
            });
        } catch (Exception ex) {
            warn("Error setting up protocol listener: " + ex);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public void log(String s) {
        getLogger().info(s);
    }

    public void warn(String s) {
        getLogger().warning(s);
    }
}
