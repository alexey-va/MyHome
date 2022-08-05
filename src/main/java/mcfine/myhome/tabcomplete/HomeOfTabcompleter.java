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

public class HomeOfTabcompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player p) {
            List<String> suggest = new ArrayList<>();
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
            return suggest;
        }
        return null;
    }
}
