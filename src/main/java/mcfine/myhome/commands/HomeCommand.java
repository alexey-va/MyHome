package mcfine.myhome.commands;

import mcfine.myhome.MyHome;
import mcfine.myhome.config.Locale;
import mcfine.myhome.utils.Home;
import mcfine.myhome.utils.HomeStorage;
import mcfine.myhome.utils.TeleportUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;

public class HomeCommand implements CommandExecutor {
    public static void homeDelete(Player p) {
        Home home = HomeStorage.getHome(p.getName().toLowerCase());
        if (home != null) {
            boolean res = HomeStorage.deleteHome(p.getName().toLowerCase());
            if (res) p.sendMessage(Locale.getString("home-deleted"));
            else p.sendMessage("Error deleting home!");
        } else {
            p.sendMessage(Locale.getString("no-home"));
        }
    }

    public static void homeSet(Player p) {
        Home home = HomeStorage.getHome(p.getName().toLowerCase());
        if (home == null) {
            boolean res = HomeStorage.putHome(new Home(p, "default"));
            if (res) p.sendMessage(Locale.getString("home-set"));
            else p.sendMessage("Error setting home!");
        } else {
            Home newHome = new Home(p, "default");
            newHome.setInvitedPlayers(home.getInvitedPlayers());
            boolean res = HomeStorage.replaceHome(p.getName().toLowerCase(), newHome);
            if (res) p.sendMessage(Locale.getString("home-moved"));
            else p.sendMessage("Error moving home!");
        }
    }

    public static void homeCommand(Player p, Integer particleId) {

        Home home = HomeStorage.getHome(p.getName().toLowerCase());

        if (home == null || home.getLocation() == null) {
            p.sendMessage(Locale.getString("no-home"));
            return;
        }

        int res = TeleportUtil.attemptTeleport(p, home.getLocation(), false, MyHome.cost, particleId, null);
        if (res == 0 && MyHome.warmup == 0) {
            if (MyHome.cost == 0.0) {
                p.sendMessage(Locale.getString("home-tp-success"));
            } else
                p.sendMessage(Locale.getString("home-tp-success") + Locale.getString("withdraw").replace("%cost%", MyHome.getEcon().format(MyHome.cost)));
        } else if (res == 1) p.sendMessage(Locale.getString("not-safe-home"));
        else if (res == 2)
            p.sendMessage(Locale.getString("not-enough-money").replace("%cost%", MyHome.getEcon().format(MyHome.cost)));
        else if (res == 3)
            p.sendMessage(Locale.getString("on-cooldown").replace("%time%", TeleportUtil.getSecondsRemaining(p) + ""));
    }

    public static void homeConfirmCommand(Player p, @Nullable String playerName, Integer particleId) {
        if (playerName == null) {
            Home home = HomeStorage.getHome(p.getName().toLowerCase());

            if (home != null) {
                int res = TeleportUtil.attemptTeleport(p, home.getLocation(), true, MyHome.cost, particleId, null);
                if (res == 0 && MyHome.warmup == 0) {
                    if (MyHome.cost == 0) p.sendMessage(Locale.getString("home-tp-success"));
                    else
                        p.sendMessage(Locale.getString("home-tp-success") + Locale.getString("withdraw").replace("%cost%", MyHome.getEcon().format(MyHome.cost)));
                } else if (res == 1) p.sendMessage(Locale.getString("not-safe-home"));
                else if (res == 2)
                    p.sendMessage(Locale.getString("not-enough-money").replace("%cost%", MyHome.getEcon().format(MyHome.cost)));
                else if (res == 3)
                    p.sendMessage(Locale.getString("on-cooldown").replace("%time%", TeleportUtil.getSecondsRemaining(p) + ""));
            } else {
                p.sendMessage(Locale.getString("no-home"));
            }
        } else {
            Home home = HomeStorage.getHome(playerName.toLowerCase());

            if (home != null) {
                if (home.isPublic() || home.isInvited(p.getName())) {
                    int res = TeleportUtil.attemptTeleport(p, home.getLocation(), true, MyHome.cost, particleId, null);
                    if (res == 0) {
                        if (MyHome.cost == 0)
                            p.sendMessage(Locale.getString("home-tp-success-other").replace("%player%", home.getPlayerName()));
                        else
                            p.sendMessage(Locale.getString("home-tp-success-other").replace("%player%", home.getPlayerName()) + Locale.getString("withdraw").replace("%cost%", MyHome.getEcon().format(MyHome.cost)));
                        home.incrementVisits();
                        HomeStorage.replaceHome(playerName.toLowerCase(), home);
                    } else if (res == 2)
                        p.sendMessage(Locale.getString("not-enough-money").replace("%player%", home.getPlayerName()).replace("%cost%", MyHome.getEcon().format(MyHome.cost)));
                    else if (res == 3)
                        p.sendMessage(Locale.getString("on-cooldown").replace("%time%", TeleportUtil.getSecondsRemaining(p) + ""));
                } else {
                    p.sendMessage(Locale.getString("not-invited").replace("%player%", playerName));
                }
            } else {
                p.sendMessage(Locale.getString("other-no-home").replace("%player%", playerName));
            }
        }
    }

    public static void homeOther(Player p, String name, Integer particleId) {
        if (name.equalsIgnoreCase(p.getName())) {
            homeCommand(p, particleId);
            return;
        }

        Home home = HomeStorage.getHome(name.toLowerCase());

        if (home != null) {
            if (home.isInvited(p.getName()) || home.isPublic()) {

                int res = TeleportUtil.attemptTeleport(p, home.getLocation(), false, MyHome.cost, particleId, null);

                if (res == 0) {

                    if (MyHome.cost == 0)
                        p.sendMessage(Locale.getString("home-tp-success-other").replace("%player%", home.getPlayerName()));
                    else
                        p.sendMessage(Locale.getString("home-tp-success-other").replace("%player%", home.getPlayerName()) + Locale.getString("withdraw").replace("%cost%", MyHome.getEcon().format(MyHome.cost)));

                    home.incrementVisits();
                    HomeStorage.replaceHome(name.toLowerCase(), home);
                } else if (res == 1)
                    p.sendMessage(Locale.getString("not-safe-home-other").replace("%player%", home.getPlayerName()));
                else if (res == 2)
                    p.sendMessage(Locale.getString("not-enough-money-other").replace("%player%", home.getPlayerName()).replace("%cost%", MyHome.getEcon().format(MyHome.cost)));
                else if (res == 3)
                    p.sendMessage(Locale.getString("on-cooldown").replace("%time%", TeleportUtil.getSecondsRemaining(p) + ""));

            } else p.sendMessage(Locale.getString("not-invited").replace("%player%", home.getPlayerName()));
        } else {
            p.sendMessage(Locale.getString("other-no-home").replace("%player%", name));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {

            // Check permissions
            if (!player.hasPermission("myhome.use")) {
                player.sendMessage(Locale.getString("no-permission"));
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("admin")) {
                if (!player.hasPermission("myhome.admin")) {
                    player.sendMessage(Locale.getString("no-permission"));
                    return true;
                }
                if (args.length == 3 && args[1].equalsIgnoreCase("tp")) {
                    boolean done = false;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().equalsIgnoreCase(args[2])) {
                            homeCommand(p, -1);
                            done = true;
                            break;
                        }
                    }
                    if (done) player.sendMessage(Locale.getString("admin-tp-success"));
                    else player.sendMessage(Locale.getString("admin-tp-no-player"));
                } else if (args.length == 4 && args[1].equalsIgnoreCase("tp") && args[3].equalsIgnoreCase("confirm")) {
                    boolean done = false;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().equalsIgnoreCase(args[2])) {
                            homeConfirmCommand(p, null, -1);
                            done = true;
                            break;
                        }
                    }
                    if (done) player.sendMessage(Locale.getString("admin-tp-success"));
                    else player.sendMessage(Locale.getString("admin-tp-no-player"));
                } else if (args.length == 4 && args[1].equalsIgnoreCase("tp")) {
                    boolean done = false;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().equalsIgnoreCase(args[2])) {
                            homeOther(p, args[3], -1);
                            done = true;
                            break;
                        }
                    }
                    if (done) player.sendMessage(Locale.getString("admin-tp-success"));
                    else player.sendMessage(Locale.getString("admin-tp-no-player"));
                } else if (args.length == 5 && args[1].equalsIgnoreCase("tp") && args[4].equalsIgnoreCase("confirm")) {
                    boolean done = false;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().equalsIgnoreCase(args[2])) {
                            homeConfirmCommand(p, args[3], -1);
                            done = true;
                            break;
                        }
                    }
                    if (done) player.sendMessage(Locale.getString("admin-tp-success"));
                    else player.sendMessage(Locale.getString("admin-tp-no-player"));
                }
            }

            if (args.length == 0) {
                homeCommand(player, null);
                return true;
            } else if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "set" -> homeSet(player);
                    case "confirm" -> homeConfirmCommand(player, null, null);
                    case "delete" -> homeDelete(player);
                    case "public" -> homePublic(player);
                    case "private" -> homePrivate(player);
                    case "help" -> homeHelp(player);
                    case "invited" -> homeInvited(player);
                    case "invites" -> homeInvites(player);
                    case "save" -> {
                        if (!player.hasPermission("myhome.admin")) player.sendMessage("no-permission");
                        try {
                            HomeStorage.saveData(true);
                        } catch (IOException e) {
                            System.out.println("Error saving data with command: " + e);
                        }
                    }
                    case "reload" -> {
                        try {
                            HomeStorage.saveData(false);
                        } catch (IOException e) {
                            System.out.println("Error saving data on reload: " + e);
                        }

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                MyHome.plugin.reloadConfig();
                                MyHome.plugin.init();
                                player.sendMessage(Locale.getString("reload"));
                            }
                        }.runTaskLater(MyHome.plugin, 20L);
                    }
                    default -> homeOther(player, args[0].toLowerCase(), null);
                }
                return true;
            } else if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "invite" -> {
                        homeInvitePlayer(player, args[1]);
                        return true;
                    }
                    case "uninvite" -> {
                        homeUninvitePlayer(player, args[1]);
                        return true;
                    }
                }

                if (args[1].equalsIgnoreCase("confirm")) {
                    homeConfirmCommand(player, args[0], null);
                    return true;
                }
            } else {
                player.sendMessage(Locale.getString("wrong-command"));
                return true;
            }
        } else {
            if (args.length > 0 && args[0].equalsIgnoreCase("admin")) {
                if (args.length == 3 && args[1].equalsIgnoreCase("tp")) {
                    boolean done = false;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().equalsIgnoreCase(args[2])) {
                            homeCommand(p, -1);
                            done = true;
                            break;
                        }
                    }
                } else if (args.length == 4 && args[1].equalsIgnoreCase("tp") && args[3].equalsIgnoreCase("confirm")) {
                    boolean done = false;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().equalsIgnoreCase(args[2])) {
                            homeConfirmCommand(p, null, -1);
                            done = true;
                            break;
                        }
                    }
                } else if (args.length == 4 && args[1].equalsIgnoreCase("tp")) {
                    boolean done = false;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().equalsIgnoreCase(args[2])) {
                            homeOther(p, args[3], -1);
                            done = true;
                            break;
                        }
                    }
                } else if (args.length == 5 && args[1].equalsIgnoreCase("tp") && args[4].equalsIgnoreCase("confirm")) {
                    boolean done = false;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().equalsIgnoreCase(args[2])) {
                            homeConfirmCommand(p, args[3], -1);
                            done = true;
                            break;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void homeUninvitePlayer(Player p, String arg) {
        Home home = HomeStorage.getHome(p.getName().toLowerCase());
        if (p.getName().equalsIgnoreCase(arg)) {
            p.sendMessage(Locale.getString("it-is-you"));
            return;
        }
        if (home != null) {
            if (!home.isInvited(arg)) {
                p.sendMessage(Locale.getString("uninvited-already").replace("%player%", arg));
            } else {
                home.uninvitePlayer(arg);
                HomeStorage.replaceHome(p.getName().toLowerCase(), home);
                p.sendMessage(Locale.getString("uninvited-player").replace("%player%", arg));

                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (pl.getName().equalsIgnoreCase(arg)) {
                        p.sendMessage(Locale.getString("you-were-uninvited").replace("%player%", p.getName()));
                        break;
                    }
                }
            }
        } else {
            p.sendMessage(Locale.getString("no-home"));
        }
    }

    private void homeInvitePlayer(Player p, String arg) {
        Home home = HomeStorage.getHome(p.getName().toLowerCase());
        if (p.getName().equalsIgnoreCase(arg)) {
            p.sendMessage(Locale.getString("it-is-you"));
            return;
        }
        if (home != null) {
            if (home.isInvited(arg)) {
                p.sendMessage(Locale.getString("invited-already").replace("%player%", arg));
            } else {
                home.invitePlayer(arg);
                HomeStorage.replaceHome(p.getName().toLowerCase(), home);
                p.sendMessage(Locale.getString("invited-player").replace("%player%", arg));

                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (pl.getName().equalsIgnoreCase(arg)) {
                        p.sendMessage(Locale.getString("you-were-invited").replace("%player%", p.getName()));
                        break;
                    }
                }
            }
        } else {
            p.sendMessage(Locale.getString("no-home"));
        }
    }

    private void homeInvites(Player p) {
        StringBuilder stringBuilder = new StringBuilder();
        int amount = 0;
        boolean atLeastOne = false;
        for (ArrayList<Home> homeList : HomeStorage.getHomeMap().values()) {
            if (homeList.size() == 0) continue;
            Home home = homeList.get(0);
            if (home == null) continue;

            if (home.isInvited(p.getName())) {
                stringBuilder.append(home.getPlayerName()).append(Locale.getString("invited-to-delimiter"));
                atLeastOne = true;
                amount++;
            }
            if (amount == 20) {
                stringBuilder.append(Locale.getString("invited-to-etc"));
                break;
            }
        }

        if (atLeastOne) {
            String result = stringBuilder.toString();
            if (amount == 20) {
                p.sendMessage(Locale.getString("invited-to-list").replace("%amount%", amount + "") + result);
            } else {
                result = Locale.getString("invited-to-list").replace("%amount%", amount + "") + result;
                p.sendMessage((result).substring(0, result.length() - Locale.getString("invited-to-delimiter").length()));
            }
        } else {
            p.sendMessage(Locale.getString("invited-by-nobody"));
        }

    }

    private void homeInvited(Player p) {
        Home home = HomeStorage.getHome(p.getName().toLowerCase());

        if (home != null) {
            int amount = 0;
            StringBuilder stringBuilder = new StringBuilder(Locale.getString("invited-list"));
            boolean atLeastOne = false;
            for (String inv : home.getInvitedPlayers()) {
                amount++;
                stringBuilder.append(inv).append(Locale.getString("invited-delimiter"));
                atLeastOne = true;
            }

            if (atLeastOne) {
                String result = stringBuilder.toString();
                p.sendMessage(result.substring(0, result.length() - Locale.getString("invited-delimiter").length()).replace("%amount%", amount + ""));
            } else {
                p.sendMessage(Locale.getString("invited-nobody"));
            }
        } else {
            p.sendMessage(Locale.getString("no-home"));
        }
    }

    private void homeHelp(Player p) {
        p.sendMessage(Locale.getString("help-message"));
    }

    private void homePrivate(Player p) {
        Home home = HomeStorage.getHome(p.getName().toLowerCase());

        if (home != null) {
            if (!home.isPublic()) {
                p.sendMessage(Locale.getString("home-already-private"));
                return;
            }
            home.setPublic(false);
            boolean res = HomeStorage.replaceHome(p.getName().toLowerCase(), home);
            if (res) p.sendMessage(Locale.getString("home-private"));
            else p.sendMessage("Error setting home private!");
        } else {
            p.sendMessage(Locale.getString("no-home"));
        }
    }

    private void homePublic(Player p) {
        Home home = HomeStorage.getHome(p.getName().toLowerCase());
        if (home != null) {
            if (home.isPublic()) {
                p.sendMessage(Locale.getString("home-already-public"));
                return;
            }
            home.setPublic(true);
            boolean res = HomeStorage.replaceHome(p.getName().toLowerCase(), home);
            if (res) p.sendMessage(Locale.getString("home-public"));
            else p.sendMessage("Error setting home public!");
        } else {
            p.sendMessage(Locale.getString("no-home"));
        }
    }

}
