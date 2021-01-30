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
        if (sender instanceof Player) {
            playerGiveCommand((Player) sender, cmd, args);
        } else {
            Bukkit.getConsoleSender().sendMessage(instance.translate("messages.no-permission.command.console"));
        }
        return true;
    }

    public boolean playerGiveCommand(final Player p, final Command cmd, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("bedrockminer")) {
            if (args.length == 0) {
                p.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.invalid"));
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (p.hasPermission("bedrockminer.give"))
                        p.getInventory().addItem(instance.getPickaxe().getItem());
                    else
                        p.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.give"));
                } else if (args[0].equalsIgnoreCase("reload")){
                    if (p.hasPermission("bedrockminer.reload")) {
                        instance.reload();
                        p.sendMessage(instance.getPrefix() + instance.translate("messages.reload"));
                    } else
                        p.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.give"));
                } else
                    p.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.invalid"));
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (p.hasPermission("bedrockminer.give")) {
                        Player target = Bukkit.getPlayerExact(args[1]);

                        if (target == null)
                            p.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.target-not-found"));
                        else
                            target.getInventory().addItem(instance.getPickaxe().getItem());
                    } else
                        p.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.give"));
                } else
                    p.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.invalid"));
            } else {
                p.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.command.to-many-args"));
            }
            return true;
        }
        return false;
    }
}
