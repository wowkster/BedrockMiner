package com.adrianwowk.bedrockminer.events;

import com.adrianwowk.bedrockminer.BedrockMiner;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.scheduler.BukkitRunnable;

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
    public void anvilShit(PrepareAnvilEvent event) {
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

        if (!allowSilk) {
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

        if (!allowOther) {
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
        ((Player) event.getView().getPlayer()).updateInventory();
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        ((Player) event.getPlayer()).updateInventory();
    }

    private void bigConditionStatement(PrepareAnvilEvent event, boolean b, boolean b2, int i, ItemStack upgradeMaterial) {
        if ((b && b2 && i > 2)
                || (!b && b2 && i > 1)
                || (b && !b2 && i > 1)
                || (!b && !b2 && i > 0))
            event.setResult(null);
    }

    @EventHandler
    public void onCraftPrepare(final PrepareItemCraftEvent event) {

        if (!instance.config.getBoolean("require-custom-bedrock"))
            return;

        if (event.getRecipe() == null || event.getRecipe().getResult() == null)
            return;

        if (event.getRecipe().getResult().hasItemMeta() && event.getRecipe().getResult().getItemMeta().hasCustomModelData() && event.getRecipe().getResult().getItemMeta().getCustomModelData() == instance.config.getInt("pickaxe.custom-model-data")) {
            for (ItemStack item : event.getInventory().getMatrix()) {
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

    @EventHandler
    public void onEnchantAttempt(final PrepareItemEnchantEvent event) {
        if (instance.getConfig().getBoolean("allow-enchanting-table"))
            return;
        ItemStack item = event.getItem();
        if (isPick(item)) {
            event.setCancelled(true);
            Player p = event.getEnchanter();
            p.updateInventory();
            new BukkitRunnable() {
                public void run() {
                    p.updateInventory();
                }
            }.runTaskLater(instance, 1L);
            if (instance.getConfig().getBoolean("enchanting-play-sound"))
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            if (instance.getConfig().getBoolean("enchanting-close-inv")) {
                new BukkitRunnable() {
                    public void run() {
                        p.closeInventory();
                    }
                }.runTaskLater(instance, 1L);
            }
            if (instance.getConfig().getBoolean("enchanting-strike-lighting")) {
                p.getWorld().strikeLightningEffect(event.getEnchantBlock().getLocation().add(0.5, 0, 0.5));
                p.spawnParticle(Particle.ENCHANTMENT_TABLE,
                        event.getEnchantBlock().getLocation().add(0.5, 0.5, 0.5),
                        200, 0.2, 0.2, 0.2, 2);
            }
        }
    }

    @EventHandler
    public void onGrindstoneAttempt(final InventoryClickEvent event) {
        if (instance.getConfig().getBoolean("allow-grindstone"))
            return;
        if (!(event.getClickedInventory() instanceof GrindstoneInventory)) {
            return;
        }
        ItemStack ing1 = event.getClickedInventory().getItem(0);
        ItemStack ing2 = event.getClickedInventory().getItem(1);
        ItemStack result = event.getCurrentItem();
        if (event.getSlotType() == InventoryType.SlotType.RESULT && (isPick(result) || isPick(ing1) || isPick(ing2))) {
            Player p = (Player) event.getWhoClicked();
            if (instance.getConfig().getBoolean("grindstone-play-sound"))
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            event.getClickedInventory().setItem(2, new ItemStack(Material.AIR));
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            p.updateInventory();
        }
    }

    @EventHandler
    public void onEntityHit(final PlayerItemDamageEvent event){
        if (isPick(event.getItem()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerHitEntity(final EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player && ((Player) event.getDamager()).getGameMode() != GameMode.CREATIVE && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && isPick(((Player) event.getDamager()).getInventory().getItemInMainHand())){
            event.setCancelled(true);
        }
    }

    private boolean isPick(ItemStack item) {
        return item != null && item.getType() == instance.getPickaxe().getItem().getType() && item.hasItemMeta() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == instance.getPickaxe().getItem().getItemMeta().getCustomModelData();
    }

    private int enchSize(ItemStack item) {
        return item.getEnchantments().size();
    }

    private boolean hasCurse(ItemStack item) {
        return item.containsEnchantment(Enchantment.VANISHING_CURSE);
    }

    private boolean hasSilk(ItemStack item) {
        return item.containsEnchantment(Enchantment.SILK_TOUCH);
    }

    private int enchSSize(ItemStack item) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        return meta.getStoredEnchants().size();
    }

    private boolean hasCurseS(ItemStack item) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        return meta.hasStoredEnchant(Enchantment.VANISHING_CURSE);
    }

    private boolean hasSilkS(ItemStack item) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        return meta.hasStoredEnchant(Enchantment.SILK_TOUCH);
    }
}