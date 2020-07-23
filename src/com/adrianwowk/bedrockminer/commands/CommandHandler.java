package com.adrianwowk.bedrockminer.commands;

import com.adrianwowk.bedrockminer.BedrockMiner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (sender instanceof Player) {
            try {
                playerGiveCommand((Player)sender, cmd, args);
            }
            catch (InstantiationException | IllegalAccessException ex2) {
                final ReflectiveOperationException ex = null;
                final ReflectiveOperationException e = ex;
                e.printStackTrace();
            }
        }
        else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You must be a player to utilize these commands!");
        }
        return true;
    }
    public boolean playerGiveCommand(final Player p, final Command cmd, final String[] args) throws InstantiationException, IllegalAccessException {
        if (cmd.getName().equalsIgnoreCase("bedrockminer")) {
            if (args[0].equalsIgnoreCase("give")) {
                if (p.hasPermission("bedrockminer.give") || p.isOp()){
                    p.getInventory().addItem(BedrockMiner.bedrockpickaxe);
                }
                else {
                    p.sendMessage(BedrockMiner.prefix + ChatColor.YELLOW + "You do not have permission to use that command.");
                }
            } else{
                p.sendMessage(BedrockMiner.prefix + ChatColor.YELLOW + "That command is invalid.");
            }

        }
        return false;
    }
}
