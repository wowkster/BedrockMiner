package com.adrianwowk.bedrockminer.events;

import com.adrianwowk.bedrockminer.BedrockMiner;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class BedrockMinerEvents implements Listener {
    @EventHandler
    public void onLeftClick(final PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getAction().equals((Object) Action.LEFT_CLICK_BLOCK)) {
            final Player player = event.getPlayer();
            ItemStack inHand = player.getItemInHand();
            if (player.hasPermission("bedrockminer.use")
                    && inHand.getType() == Material.NETHERITE_PICKAXE
                    && inHand.getItemMeta().hasEnchant(Enchantment.VANISHING_CURSE)
                    && inHand.getItemMeta().hasLore()) {
                final Block block = event.getClickedBlock();
                final BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
                Bukkit.getServer().getPluginManager().callEvent((Event)breakEvent);
                if (breakEvent.isCancelled()) {
                    return;
                }
                if (block.getType().equals((Object)Material.BEDROCK)) {
                    block.breakNaturally();
                    block.setType(Material.AIR);
                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 1.0f, 1.0f);
                    if (player.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)){
                        ItemStack item = new ItemStack(Material.BEDROCK, 1);
                        player.getInventory().addItem(item);
                    }
                    final ItemStack newItem = player.getItemInHand();
                    final Short durability = (short)(newItem.getDurability() + 10);
                    newItem.setDurability((short)durability);
                    //this.getServer().getLogger().info(durability.toString());
                    if (durability >= newItem.getType().getMaxDurability()) {
                        player.setItemInHand(new ItemStack(Material.AIR));
                        return;
                    }
                    player.setItemInHand(newItem);
                } else {
                    event.setCancelled(true);
                    player.sendMessage(BedrockMiner.prefix + ChatColor.YELLOW + "Warning! You can only break "
                            + ChatColor.AQUA + "Bedrock"
                            + ChatColor.YELLOW + " using this pickaxe");
                }
            }
            else {
                final Block block = event.getClickedBlock();
                final BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
                Bukkit.getServer().getPluginManager().callEvent((Event)breakEvent);
                if (breakEvent.isCancelled()) {
                    return;
                }if (inHand.getType() == Material.NETHERITE_PICKAXE
                        && inHand.getItemMeta().hasEnchant(Enchantment.VANISHING_CURSE)
                        && inHand.getItemMeta().hasLore()){
                    if (!block.getType().equals((Object)Material.BEDROCK)) {
                        player.sendMessage(BedrockMiner.prefix + ChatColor.YELLOW + "Warning! You can only break "
                                + ChatColor.AQUA + "Bedrock"
                                + ChatColor.YELLOW + " using this pickaxe");
                    } else{

                    }
                    player.sendMessage(BedrockMiner.prefix + ChatColor.YELLOW + "You do not have permission to use this pickaxe.");
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onCraftAtempt(final CraftItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Player player = (Player)event.getWhoClicked();
        if (event.getCurrentItem().equals(BedrockMiner.bedrockpickaxe)) {
            if (!player.hasPermission("bedrockminer.craft.pickaxe")) {
                event.setCancelled(true);
                event.setCurrentItem(new ItemStack(Material.AIR));
                player.sendMessage(BedrockMiner.prefix + ChatColor.YELLOW + "You do not have permission to craft this pickaxe.");
                return;
            }
        } else if (event.getCurrentItem().equals(BedrockMiner.bedrock)){
            if (!player.hasPermission("bedrockminer.craft.bedrock")) {
                event.setCancelled(true);
                event.setCurrentItem(new ItemStack(Material.AIR));
                player.sendMessage(BedrockMiner.prefix + ChatColor.YELLOW + "You do not have permission to craft bedrock.");
                return;
            }
        }
        return;
    }
}
