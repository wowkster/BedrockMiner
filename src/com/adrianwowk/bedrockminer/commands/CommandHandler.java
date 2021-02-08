package com.adrianwowk.bedrockminer.commands;

import com.adrianwowk.bedrockminer.BedrockMiner;
import com.adrianwowk.bedrockminer.BedrockPickaxe;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    private final BedrockMiner instance;

    public CommandHandler(BedrockMiner plugin) {
        this.instance = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (cmd.getName().equalsIgnoreCase("bedrockminer")) {
            if (args.length == 0) {
                sender.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.invalid"));
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (sender.hasPermission("bedrockminer.give"))
                        if (sender instanceof Player)
                            ((Player)sender).getInventory().addItem(instance.getPickaxe().getItem());
                        else
                           Bukkit.getConsoleSender().sendMessage(instance.translate("messages.no-permission.command.console"));
                    else
                        sender.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.give"));
                } else if (args[0].equalsIgnoreCase("reload")){
                    if (sender.hasPermission("bedrockminer.reload")) {
                        instance.reload();
                        sender.sendMessage(instance.getPrefix() + instance.translate("messages.reload"));
                    } else
                        sender.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.give"));
                } else
                    sender.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.invalid"));
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (sender.hasPermission("bedrockminer.give")) {
                        Player target = Bukkit.getPlayerExact(args[1]);

                        if (target == null)
                            sender.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.target-not-found"));
                        else
                            target.getInventory().addItem(instance.getPickaxe().getItem());
                    } else
                        sender.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.give"));
                } else
                    sender.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.invalid"));
            } else {
                sender.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.to-many-args"));
            }
            return true;
        }
        return false;
    }

}