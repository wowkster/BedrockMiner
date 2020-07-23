// 
// Decompiled by Procyon v0.5.36
// 

package com.adrianwowk.bedrockminer;

import com.adrianwowk.bedrockminer.commands.BMTabCompleter;
import com.adrianwowk.bedrockminer.commands.CommandHandler;
import com.adrianwowk.bedrockminer.events.BedrockMinerEvents;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BedrockMiner extends JavaPlugin
{
    Server server;
    ConsoleCommandSender console;
    public static String prefix;
    public static ItemStack bedrockpickaxe;
    public static ItemStack bedrock;
    public static ShapedRecipe bedrockpickaxeRecipe;
    public static ShapedRecipe bedrockRecipe;

    public BedrockMiner() {
        this.server = Bukkit.getServer();
        this.console = this.server.getConsoleSender();
        this.prefix = ChatColor.GRAY + "[" + ChatColor.RED + "BedrockMiner" + ChatColor.GRAY + "] ";
    }

    public void onEnable() {
        // Initialize Bedrock Pickaxe Item
        initPickaxe();

        // Initialize Bedrock Crafting Recipe
        initBedrock();

        // Register command tab completer and executer

        getCommand("bedrockminer").setTabCompleter(new BMTabCompleter());
        getCommand("bedrockminer").setExecutor(new CommandHandler());

        // Register Event Listeners
        Bukkit.getServer().getPluginManager().registerEvents(new BedrockMinerEvents(), (Plugin) this);

        // Server Console Message
        this.getLogger().info(ChatColor.GREEN + "=================================");
        this.getLogger().info(ChatColor.GREEN + "         [BedrockMiner]          ");
        this.getLogger().info(ChatColor.GREEN + "  Has been successfuly enabled!  ");
        this.getLogger().info(ChatColor.GREEN + "     Author - Adrian Wowk        ");
        this.getLogger().info(ChatColor.GREEN + "=================================");

    }

    private void initPickaxe() {
        bedrockpickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta bedrockpickaxeLabel = bedrockpickaxe.getItemMeta();
        assert bedrockpickaxeLabel != null;
        bedrockpickaxeLabel.setDisplayName(ChatColor.GOLD + "Bedrock Pickaxe");
        bedrockpickaxeLabel.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        List<String> lore = new ArrayList<>();
        lore.add("Can Break Bedrock");
        bedrockpickaxeLabel.setLore(lore);
        bedrockpickaxe.setItemMeta(bedrockpickaxeLabel);

        NamespacedKey bedrockpickaxeKey = new NamespacedKey((Plugin) this, "bedrockpickaxe");
        bedrockpickaxeRecipe = new ShapedRecipe(bedrockpickaxeKey, bedrockpickaxe);

        bedrockpickaxeRecipe.shape(
                "BBB",
                " S ",
                " S ");
        bedrockpickaxeRecipe.setIngredient('B', Material.BEDROCK);
        bedrockpickaxeRecipe.setIngredient('S', Material.STICK);
        Bukkit.addRecipe(bedrockpickaxeRecipe);
    }

    private void initBedrock() {
        bedrock = new ItemStack(Material.BEDROCK);
        ItemMeta bedrockLabel = bedrock.getItemMeta();
        assert bedrockLabel != null;
        bedrockLabel.setDisplayName(ChatColor.GOLD + "Bedrock");
        bedrock.setItemMeta(bedrockLabel);

        NamespacedKey bedrockKey = new NamespacedKey((Plugin) this, "bedrock");
        bedrockRecipe = new ShapedRecipe(bedrockKey, bedrock);

        bedrockRecipe.shape(
                "OOO",
                "OAO",
                "OOO");
        bedrockRecipe.setIngredient('O', Material.OBSIDIAN);
        bedrockRecipe.setIngredient('A', Material.DIAMOND);
        Bukkit.addRecipe(bedrockRecipe);
    }
}