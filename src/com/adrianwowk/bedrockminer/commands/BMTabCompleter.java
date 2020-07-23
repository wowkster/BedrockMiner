package com.adrianwowk.bedrockminer.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BMTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bedrockminer")){
            if (sender instanceof Player){
                Player p = (Player) sender;

                List<String> list = new ArrayList<>();
                if (p.hasPermission("bedrockminer.give")){
                    list.add("give");
                }

                return list;
            }
        }
        return null;
    }
}
