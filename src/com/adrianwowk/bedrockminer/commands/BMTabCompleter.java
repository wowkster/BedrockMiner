package com.adrianwowk.bedrockminer.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BMTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        List<String> list = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("bedrockminer")) {
            if (args.length <= 1){
                if (sender.hasPermission("bedrockminer.give"))
                    list.add("give");
                if (sender.hasPermission("bedrockminer.reload"))
                    list.add("reload");
            } else if (args.length == 2) {
                if (sender.hasPermission("bedrockminer.give") && args[0].equalsIgnoreCase("give"))
                    for (Player p : Bukkit.getOnlinePlayers()){
                        list.add(p.getName());
                    }
            }
        }

        return list;
    }
}