package com.adrianwowk.bedrockminer.commands;

import com.adrianwowk.bedrockminer.BedrockMiner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    private BedrockMiner instance;

    public CommandHandler(BedrockMiner plugin){
        this.instance = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (sender instanceof Player) {
                playerGiveCommand((Player)sender, cmd, args);
        }
        else {
            Bukkit.getConsoleSender().sendMessage(instance.translate("messages.no-permission.command.console") );
        }
        return true;
    }
    public boolean playerGiveCommand(final Player p, final Command cmd, final String[] args){
        if (cmd.getName().equalsIgnoreCase("bedrockminer")) {
            if (args[0].equalsIgnoreCase("give")) {
                if (p.hasPermission("bedrockminer.give")){
                    p.getInventory().addItem(instance.bedrockpickaxe);
                }
                else {
                    p.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.give"));
                }
            } else{
                p.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.invalid"));
            }

        }
        return false;
    }
}
