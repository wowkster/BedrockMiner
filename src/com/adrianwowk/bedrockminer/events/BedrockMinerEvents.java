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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BedrockMinerEvents implements Listener {

    private final BedrockMiner instance;

    public BedrockMinerEvents(BedrockMiner instance) {
        this.instance = instance;
    }

    @EventHandler
    public void playerJoin(final PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (!player.hasDiscoveredRecipe(instance.bedrockKey))
            player.discoverRecipe(instance.bedrockKey);
        if (!player.hasDiscoveredRecipe(instance.bedrockpickaxeKey))
            player.discoverRecipe(instance.bedrockpickaxeKey);
    }

    @EventHandler
    public void onLeftClick(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack inHand = event.getItem();

        if (inHand == null || inHand.getType() == Material.AIR)
            return;

        int modelData;
        if (!Objects.requireNonNull(inHand.getItemMeta()).hasCustomModelData()){
            return;
        }

        modelData = inHand.getItemMeta().getCustomModelData();

        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            return;
        }

        if (modelData != Objects.requireNonNull(instance.bedrockpickaxe.getItemMeta()).getCustomModelData()){
            return;
        }

        if (!player.hasPermission("bedrockminer.use")) {
            player.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.use.pickaxe"));
            event.setCancelled(true);
            return;
        }

        final Block block = event.getClickedBlock();
        assert block != null;
        if (block.getY() == 0 && !instance.getConfig().getBoolean("breakbottom")) {
            player.sendMessage(instance.getPrefix() + instance.translate("messages.breakbottom"));
            return;
        }

        if (block.getType() != Material.BEDROCK) {
            event.setCancelled(true);
            player.sendMessage(instance.getPrefix() + instance.translate("messages.not-bedrock"));
            return;
        }
        block.breakNaturally();
        block.setType(Material.AIR);
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 1.0f, 1.0f);
        if (inHand.getEnchantments().containsKey(Enchantment.SILK_TOUCH) && instance.getConfig().getBoolean("silktouch")) {
            ItemStack item = new ItemStack(Material.BEDROCK);
            Objects.requireNonNull(player.getLocation().getWorld()).dropItemNaturally(block.getLocation(), item);
        }
        ItemStack newItem = player.getInventory().getItemInMainHand();
        final short durability = (short) (newItem.getDurability() + instance.getConfig().getInt("durability"));
        newItem.setDurability(durability);
        if (durability >= newItem.getType().getMaxDurability()) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            return;
        }
        player.getInventory().setItemInMainHand(newItem);

    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event){
        Player player = event.getPlayer();
        ItemStack inHand = player.getInventory().getItemInMainHand();

        if (inHand.getType() == Material.AIR)
            return;

        int modelData;
        if (!Objects.requireNonNull(inHand.getItemMeta()).hasCustomModelData()){
            return;
        }

        modelData = inHand.getItemMeta().getCustomModelData();

        if (modelData != Objects.requireNonNull(instance.bedrockpickaxe.getItemMeta()).getCustomModelData()){
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onCraftAttempt(final CraftItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Player player = (Player) event.getWhoClicked();
        if (Objects.equals(event.getCurrentItem(), instance.bedrockpickaxe)) {
            if (!player.hasPermission("bedrockminer.craft.pickaxe")) {
                event.setCancelled(true);
                event.setCurrentItem(new ItemStack(Material.AIR));
                player.updateInventory();
                player.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.craft.pickaxe"));
            }
        } else if (Objects.equals(event.getCurrentItem(), instance.bedrock)) {
            if (!player.hasPermission("bedrockminer.craft.bedrock")) {
                event.setCancelled(true);
                event.setCurrentItem(new ItemStack(Material.AIR));
                player.updateInventory();
                player.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.craft.bedrock"));
            }
        }
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
}