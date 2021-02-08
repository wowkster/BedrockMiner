package com.adrianwowk.bedrockminer.events;

import com.adrianwowk.bedrockminer.BedrockMiner;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

public class BedrockMinerEvents implements Listener {

    private final BedrockMiner instance;

    public BedrockMinerEvents(BedrockMiner instance) {
        this.instance = instance;
    }

    @EventHandler
    public void playerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasDiscoveredRecipe(new NamespacedKey(instance, "bedrock")))
            player.discoverRecipe(new NamespacedKey(instance, "bedrock"));
        if (!player.hasDiscoveredRecipe(new NamespacedKey(instance, "bedrock_pickaxe")))
            player.discoverRecipe(new NamespacedKey(instance, "bedrock_pickaxe"));
    }

    @EventHandler
    public void anvilShit(PrepareAnvilEvent event){
        ItemStack baseMaterial = event.getInventory().getItem(0);
        ItemStack upgradeMaterial = event.getInventory().getItem(1);
        ItemStack result = event.getResult();

        if (baseMaterial == null
//                || upgradeMaterial == null
                || result == null
                || !result.hasItemMeta()
                || !result.getItemMeta().hasCustomModelData()
                || result.getItemMeta().getCustomModelData() != instance.config.getInt("pickaxe.custom-model-data"))
            return;

        boolean allowSilk = instance.config.getBoolean("allow-anvil-silk");
        boolean allowOther = instance.config.getBoolean("allow-anvil-other");
        boolean allowRename = instance.config.getBoolean("allow-anvil-rename");
        boolean allowMerge = instance.config.getBoolean("allow-anvil-merge");

        if (!allowSilk){
            if (upgradeMaterial != null) {
                if (upgradeMaterial.containsEnchantment(Enchantment.SILK_TOUCH))
                    event.setResult(null);

                if (baseMaterial.containsEnchantment(Enchantment.SILK_TOUCH))
                    event.setResult(null);

                if (upgradeMaterial.getType() == Material.ENCHANTED_BOOK) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) upgradeMaterial.getItemMeta();
                    if (meta.hasStoredEnchant(Enchantment.SILK_TOUCH))
                        event.setResult(null);
                }
            }
        }

        if (!allowOther){
            if (upgradeMaterial != null) {
                bigConditionStatement(event, hasSilk(upgradeMaterial), hasCurse(upgradeMaterial), enchSize(upgradeMaterial), upgradeMaterial);

                if (upgradeMaterial.getType() == Material.ENCHANTED_BOOK)
                    bigConditionStatement(event, hasSilkS(upgradeMaterial), hasCurseS(upgradeMaterial), enchSSize(upgradeMaterial), upgradeMaterial);
            }
        }

        if (!allowRename)
            if (baseMaterial.hasItemMeta())
                if (baseMaterial.getItemMeta().hasDisplayName())
                    if (!ChatColor.stripColor(event.getInventory().getRenameText()).equals(ChatColor.stripColor(baseMaterial.getItemMeta().getDisplayName())))
                        event.setResult(null);

        if (!allowMerge)
            if (upgradeMaterial != null)
                if (baseMaterial.getType() == Material.NETHERITE_PICKAXE && upgradeMaterial.getType() == Material.NETHERITE_PICKAXE)
                    event.setResult(null);
        ((Player)event.getView().getPlayer()).updateInventory();
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event){
        ((Player) event.getPlayer()).updateInventory();
    }

    private void bigConditionStatement(PrepareAnvilEvent event, boolean b, boolean b2, int i, ItemStack upgradeMaterial) {
        if ((b && b2 && i > 2)
        || (!b && b2 && i > 1)
        || ( b && !b2 && i > 1)
        || (!b && !b2 && i > 0))
            event.setResult(null);
    }
    @EventHandler
    public void onCraftPrepare(final PrepareItemCraftEvent event) {

        if (!instance.config.getBoolean("require-custom-bedrock"))
            return;

        if (event.getRecipe() == null || event.getRecipe().getResult() == null)
            return;

        if (event.getRecipe().getResult().hasItemMeta() && event.getRecipe().getResult().getItemMeta().hasCustomModelData() && event.getRecipe().getResult().getItemMeta().getCustomModelData() == instance.config.getInt("pickaxe.custom-model-data")){
            for (ItemStack item : event.getInventory().getMatrix()){
                if (item != null) {
                    if (item.getType() == Material.BEDROCK && !item.getItemMeta().hasCustomModelData()) {
                        event.getInventory().setResult(new ItemStack(Material.AIR));
                        ((Player) event.getView().getPlayer()).updateInventory();
                    }
                }
            }
        }

    }

    @EventHandler
    public void onCraftAttempt(final CraftItemEvent event) {
        if (event.isCancelled())
            return;
        final Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null)
            return;

        if (event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasCustomModelData()
                && event.getCurrentItem().getItemMeta().getCustomModelData()
                == instance.config.getInt("pickaxe.custom-model-data")) {
            if (!player.hasPermission("bedrockminer.craft.pickaxe")) {
                event.setCancelled(true);
                event.setCurrentItem(new ItemStack(Material.AIR));
                player.updateInventory();
                player.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.craft.pickaxe"));
            }
        } else if (event.getCurrentItem().hasItemMeta()
                && event.getCurrentItem().getItemMeta().hasCustomModelData()
                && event.getCurrentItem().getItemMeta().getCustomModelData()
                == instance.config.getInt("bedrock.custom-model-data")) {
            if (!player.hasPermission("bedrockminer.craft.bedrock")) {
                event.setCancelled(true);
                event.setCurrentItem(new ItemStack(Material.AIR));
                player.updateInventory();
                player.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.craft.bedrock"));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            }
        }
    }

    private int enchSize(ItemStack item){
        return item.getEnchantments().size();
    }

    private boolean hasCurse(ItemStack item){
        return item.containsEnchantment(Enchantment.VANISHING_CURSE);
    }

    private boolean hasSilk(ItemStack item){
        return item.containsEnchantment(Enchantment.SILK_TOUCH);
    }

    private int enchSSize(ItemStack item){
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        return meta.getStoredEnchants().size();
    }

    private boolean hasCurseS(ItemStack item){
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        return meta.hasStoredEnchant(Enchantment.VANISHING_CURSE);
    }

    private boolean hasSilkS(ItemStack item){
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        return meta.hasStoredEnchant(Enchantment.SILK_TOUCH);
    }
}