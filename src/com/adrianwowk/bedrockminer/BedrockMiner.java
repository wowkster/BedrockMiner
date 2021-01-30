package com.adrianwowk.bedrockminer;

import com.adrianwowk.bedrockminer.commands.BMTabCompleter;
import com.adrianwowk.bedrockminer.commands.CommandHandler;
import com.adrianwowk.bedrockminer.events.BedrockMinerEvents;
import com.adrianwowk.bedrockminer.utils.Metrics;
import com.adrianwowk.bedrockminer.utils.UpdateChecker;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BedrockMiner extends JavaPlugin{
    public FileConfiguration config = getConfig();
    Server server;
    ConsoleCommandSender console;
    private BedrockPickaxe pickaxe;
    private CustomBedrock bedrock;

    public BedrockMiner() {
        this.server = Bukkit.getServer();
        this.console = this.server.getConsoleSender();
        this.pickaxe = new BedrockPickaxe(this);
        this.bedrock = new CustomBedrock(this);
    }

    public void onEnable() {
        this.saveDefaultConfig();

        // Register command tab completer and executer

        getCommand("bedrockminer").setTabCompleter(new BMTabCompleter());
        getCommand("bedrockminer").setExecutor(new CommandHandler(this));

        // Register Event Listeners
        server.getPluginManager().registerEvents(new BedrockMinerEvents(this), this);
        server.getPluginManager().registerEvents(pickaxe, this);
        server.getPluginManager().registerEvents(bedrock, this);

        new UpdateChecker(this, 81335).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                console.sendMessage(getPrefix() + ChatColor.YELLOW + "Your version is up to date :)");
            } else {
                console.sendMessage(getPrefix() + ChatColor.LIGHT_PURPLE + "There is a new update available for this plugin.");
                console.sendMessage(getPrefix() + ChatColor.LIGHT_PURPLE + "Download the latest version (" + version + ") from " + ChatColor.YELLOW + UpdateChecker.getLink());
            }
        });
        // Enable Plugin Metrics with bStats
        Metrics metrics = new Metrics(this, 10029);

//        logHostNames(metrics);

        // Server Console Message
        console.sendMessage(getPrefix() + "Successfully enabled :)");

//        new BukkitRunnable(){
//            public void run() {
//                console.sendMessage(getPrefix() + "You are not running the licensed version of this plugin :(");
//                Bukkit.broadcastMessage(getPrefix() + "This server is not running the licensed version of this plugin :(");
//            }
//        }.runTaskTimerAsynchronously(this, 10L, 20L * 30);
//
//        new BukkitRunnable() {
//
//            public void run() {
//                Bukkit.getPluginManager().disablePlugin(BedrockMiner.getPlugin(BedrockMiner.class));
//            }
//        }.runTaskLaterAsynchronously(this, 20L * 60 * 10);
    }

    public void onDisable(){
        console.sendMessage(getPrefix() + "Successfully disabled :)");
    }

    public void reload(){
        reloadConfig();
        this.config = getConfig();
    }

    public String getPrefix() {
        return translate("messages.prefix");
    }

    public String translate(String path) {
            return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(path));
    }

    public BedrockPickaxe getPickaxe(){
        return this.pickaxe;
    }

    public CustomBedrock getBedrock(){
        return this.bedrock;
    }
}