package mcfine.myhome.tabcomplete;

import mcfine.myhome.utils.Home;
import mcfine.myhome.utils.HomeStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeTabcompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player p) {
            if (args.length == 1) {
                List<String> suggest = new ArrayList<>();
                if ("set".indexOf(args[0].toLowerCase()) == 0) suggest.add("set");
                if ("invite".indexOf(args[0].toLowerCase()) == 0) suggest.add("invite");
                if ("uninvite".indexOf(args[0].toLowerCase()) == 0) suggest.add("uninvite");
                if ("invites".indexOf(args[0].toLowerCase()) == 0) suggest.add("invites");
                if ("delete".indexOf(args[0].toLowerCase()) == 0) suggest.add("delete");
                if ("help".indexOf(args[0].toLowerCase()) == 0) suggest.add("help");
                if ("public".indexOf(args[0].toLowerCase()) == 0) suggest.add("public");
                if ("invited".indexOf(args[0].toLowerCase()) == 0) suggest.add("invited");
                if ("private".indexOf(args[0].toLowerCase()) == 0) suggest.add("private");
                if ("confirm".indexOf(args[0].toLowerCase()) == 0) suggest.add("confirm");
                if (suggest.size() == 0) {
                    for (ArrayList<Home> array : HomeStorage.getHomeMap().values()) {
                        if (array == null) continue;
                        for (Home home : array) {
                            if (home == null) continue;
                            if ((home.isInvited(p.getName()) || home.isPublic() || p.hasPermission("mcfinehome.admin")) && (home.getPlayerName().toLowerCase().indexOf(args[0].toLowerCase()) == 0)) {
                                suggest.add(home.getPlayerName());
                            }
                        }
                        if (suggest.size() > 60) break;
                    }
                }
                return suggest;
            } else if (args.length == 2 && args[0].toLowerCase(Locale.ROOT).trim().equals("uninvite")) {
                List<String> suggest = new ArrayList<>();
                Home main = HomeStorage.getHome(p.getName());
                if (main != null) {
                    suggest = main.getInvitedPlayers();
                }
                return suggest;
            } else if (args.length == 2) {
                List<String> suggest = new ArrayList<String>();
                if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("delete") ||
                        args[0].equalsIgnoreCase("invited") || args[0].equalsIgnoreCase("invites") || args[0].equalsIgnoreCase("private") ||
                        args[0].equalsIgnoreCase("public") || args[0].equalsIgnoreCase("help")) {
                    return new ArrayList<>();
                } else if (args[0].equalsIgnoreCase("invite")) {
                    return null;
                }
                suggest.add("confirm");
                return suggest;
            }
        }

        return null;
    }
}
