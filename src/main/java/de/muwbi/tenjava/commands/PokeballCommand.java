package de.muwbi.tenjava.commands;

import de.muwbi.tenjava.PokeballZ;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PokeballCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Can't use this as console");
            return true;
        }

        Player player = (Player) commandSender;

        if(PokeballZ.getInstance().getLastGets().containsKey(player.getName())) {
            if((System.currentTimeMillis() - PokeballZ.getInstance().getLastGets().get(player.getName())) / 1000 / 60
                    < PokeballZ.getInstance().getConfig().getInt("settings.cooldown-time") && !player.hasPermission("pokeballz.admin")) {
                player.sendMessage(PokeballZ.PREFIX + ChatColor.RED + "You have to wait a little bit, to use this again!");
                return true;
            }
        }

        player.getInventory().addItem(PokeballZ.getInstance().getItemStack(PokeballZ.getInstance().getConfig().getInt("settings.amount")));
        player.sendMessage(PokeballZ.PREFIX + ChatColor.GREEN + "Here you go!");

        PokeballZ.getInstance().getLastGets().put(player.getName(), System.currentTimeMillis());

        return true;
    }

}
