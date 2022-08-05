package mcfine.myhome.commands;

import mcfine.myhome.config.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomeOfCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player p) {
            if (args.length == 1) {
                HomeCommand.homeOther(p, args[0].toLowerCase(), null);
                return true;
            } else if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
                HomeCommand.homeConfirmCommand(p, args[0], null);
                return true;
            } else {
                p.sendMessage(Locale.getString("wrong-command"));
            }
        } else {
            System.out.println("No non player implementation for the command: /home-of");
        }

        return false;
    }
}
