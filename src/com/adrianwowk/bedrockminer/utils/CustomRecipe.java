package com.adrianwowk.bedrockminer.utils;

import com.adrianwowk.bedrockminer.BedrockMiner;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class CustomRecipe {

    private NamespacedKey key;
    private String path;
    private ItemStack item;
    private BedrockMiner plugin;

    public CustomRecipe(String key, String path, ItemStack item, BedrockMiner plugin){
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, key);
        this.path = path;
        this.item = item;
    }
    public ShapedRecipe getShapedRecipe(){

        String alphabet = "abcdefghijkl";

        List<String> strings = plugin.config.getStringList(this.path);

        HashMap<Material, String> materials = new HashMap<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Material> itemList = new ArrayList<>();


        for (String line : strings){
            items.addAll(Arrays.asList(line.trim().split(",")));
        }
        int count = 0;
        for (String item : items){
            materials.putIfAbsent(Material.valueOf(item.trim().toUpperCase()),String.valueOf(alphabet.charAt(count)));
            itemList.add(Material.valueOf(item.trim().toUpperCase()));
            count++;
        }

        String[] lines = new String[]{"", "", ""};

        for (int i = 0; i < 3; i ++){
            for (int j = 0; j < 3; j++){
                Material mat = itemList.get((i * 3) + j);
                lines[i] += (mat == Material.AIR ? " " : materials.get(mat));
            }
        }

        ShapedRecipe recipe = new ShapedRecipe(key, item);

        recipe.shape(lines[0], lines[1], lines[2]);

        for (Map.Entry<Material, String> entry : materials.entrySet()) {
            Material key = entry.getKey();
            String value = entry.getValue();
            if (key != Material.AIR)
                recipe.setIngredient(value.charAt(0), key);
        }

        return recipe;
    }
}