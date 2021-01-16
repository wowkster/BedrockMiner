package com.adrianwowk.bedrockminer;

import com.adrianwowk.bedrockminer.commands.BMTabCompleter;
import com.adrianwowk.bedrockminer.commands.CommandHandler;
import com.adrianwowk.bedrockminer.events.BedrockMinerEvents;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public class BedrockMiner extends JavaPlugin
{
    FileConfiguration config = getConfig();
    Server server;
    ConsoleCommandSender console;
    public ItemStack bedrockpickaxe;
    public ItemStack bedrock;
    public ShapedRecipe bedrockpickaxeRecipe;
    public ShapedRecipe bedrockRecipe;

    public NamespacedKey bedrockKey;
    public NamespacedKey bedrockpickaxeKey;

    public BedrockMiner() {
        this.server = Bukkit.getServer();
        this.console = this.server.getConsoleSender();
    }

    public void onEnable() {
        this.saveDefaultConfig();

        config.getBoolean("breakbottom");
        config.getBoolean("silktouch");
        config.getInt("durability");

        // Initialize Bedrock Pickaxe Item
        initPickaxe();

        // Initialize Bedrock Crafting Recipe
        initBedrock();

        // Register command tab completer and executer

        getCommand("bedrockminer").setTabCompleter(new BMTabCompleter());
        getCommand("bedrockminer").setExecutor(new CommandHandler(this));

        // Register Event Listeners
        Bukkit.getServer().getPluginManager().registerEvents(new BedrockMinerEvents(this), this);

        // Server Console Message
        console.sendMessage(getPrefix() + "Successfully enabled :)");
    }

    public String getPrefix(){
        return translate("messages.prefix");
    }

    public String translate(String path) {
        return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(path));
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
        bedrockpickaxeLabel.setCustomModelData(6969696);
        bedrockpickaxe.setItemMeta(bedrockpickaxeLabel);

        bedrockpickaxeKey = new NamespacedKey(this, "bedrockpickaxe");
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

        bedrockKey = new NamespacedKey((Plugin) this, "bedrock");
        bedrockRecipe = new ShapedRecipe(bedrockKey, bedrock);

        bedrockRecipe.shape(
                "OOO",
                "ODO",
                "OOO");
        bedrockRecipe.setIngredient('O', Material.OBSIDIAN);
        bedrockRecipe.setIngredient('D', Material.DIAMOND);
        Bukkit.addRecipe(bedrockRecipe);
    }
}