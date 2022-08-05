package mcfine.myhome.commands;

import mcfine.myhome.config.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DelHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player p) {
            if (args.length == 0) {
                HomeCommand.homeDelete(p);
            } else {
                p.sendMessage(Locale.getString("wrong-command"));
            }
        } else {
            System.out.println("No non player implementation for the command: /delhome");
            return true;
        }

        return false;
    }
}
