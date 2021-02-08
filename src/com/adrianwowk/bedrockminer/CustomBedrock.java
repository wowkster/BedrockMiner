package com.adrianwowk.bedrockminer;

import com.adrianwowk.bedrockminer.utils.CustomRecipe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CustomBedrock implements Listener {

    private BedrockMiner instance;
    private ItemStack item;

    public CustomBedrock(BedrockMiner plugin){
        this.instance = plugin;
        this.item = initBedrock();
    }

    private ItemStack initBedrock() {
        ItemStack bedrock = new ItemStack(Material.BEDROCK);
        ItemMeta bedrockLabel = bedrock.getItemMeta();
        assert bedrockLabel != null;
        bedrockLabel.setDisplayName(instance.translate("bedrock.name"));
        List<String> lore = new ArrayList<>();
        for (String str : instance.config.getStringList("bedrock.lore"))
            lore.add(ChatColor.translateAlternateColorCodes('&', str));
        bedrockLabel.setLore(lore);
        bedrockLabel.setCustomModelData(instance.config.getInt("bedrock.custom-model-data"));

        bedrock.setItemMeta(bedrockLabel);

        if (instance.config.getBoolean("pickaxe-recipe")) {
            Bukkit.removeRecipe(new NamespacedKey(instance, "bedrock"));
            Bukkit.addRecipe(new CustomRecipe("bedrock", "bedrock-shaped-recipe", bedrock, instance).getShapedRecipe());
        }
        return bedrock;
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.BEDROCK) {
            if (!event.getPlayer().hasPermission("bedrockminer.place")) {
                event.getPlayer().sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.use.bedrock"));
                event.setCancelled(true);
            }
        }
    }

    public ItemStack getItem(){
        this.item = initBedrock();
        return this.item;
    }

}