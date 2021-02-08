package com.adrianwowk.bedrockminer;

import com.adrianwowk.bedrockminer.utils.CustomRecipe;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BedrockPickaxe implements Listener {

    private BedrockMiner instance;
    private final Map<UUID, Long> playerCoolDownMap;
    private ItemStack item;

    public BedrockPickaxe(BedrockMiner plugin){
        this.instance = plugin;
        this.playerCoolDownMap = new HashMap<>();
        this.item = initPickaxe();
    }

    @EventHandler
    public void onLeftClick(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack inHand = event.getItem();

        if (inHand == null || inHand.getType() == Material.AIR)
            return;

        int modelData;
        if (!Objects.requireNonNull(inHand.getItemMeta()).hasCustomModelData())
            return;

        modelData = inHand.getItemMeta().getCustomModelData();

        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            return;

        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        if (modelData != instance.config.getInt("pickaxe.custom-model-data"))
            return;

        if (!player.hasPermission("bedrockminer.use")) {
            player.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.use.pickaxe"));
            event.setCancelled(true);
            return;
        }

        final Block block = event.getClickedBlock();
        assert block != null;
        List<Integer> disabledLayers = instance.getConfig().getIntegerList("disabled-layers");

        if (block.getType() != Material.BEDROCK)
            return;

        List<String> disabledWorlds = instance.getConfig().getStringList("disabled-worlds");

        World w = player.getWorld();
        for (String str : disabledWorlds) {
            if (w.getName().equalsIgnoreCase(str)) {
                player.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.world"));
                return;
            }
        }

        for (Integer layer : disabledLayers)
            if (block.getY() == layer) {
                player.sendMessage(instance.getPrefix() + instance.translate("messages.no-permission.break"));
                return;
            }

        if (playerIsOnCoolDown(player)) {
            String msg = instance.translate("messages.no-permission.cooldown")
                    .replace("%TICKS%", String.valueOf(getCoolDownTimeRemaining(player)))
                    .replace("%SECONDS%", String.valueOf((int) Math.ceil((double) getCoolDownTimeRemaining(player) / 20)));
            if (!msg.equals(""))
                player.sendMessage(instance.getPrefix() + msg);
            return;
        }

        BlockBreakEvent bbe = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(bbe);
        if (bbe.isCancelled())
            return;
//        block.breakNaturally();
//        block.setType(Material.AIR);
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 1.0f, 1.0f);
        if (inHand.getEnchantments().containsKey(Enchantment.SILK_TOUCH) &&
                inHand.getEnchantmentLevel(Enchantment.SILK_TOUCH) >= instance.getConfig().getInt("silk-touch-level") &&
                instance.getConfig().getBoolean("silk-touch")) {
            player.getLocation().getWorld().dropItemNaturally(block.getLocation(), instance.getBedrock().getItem());
        }
        ItemStack newItem = player.getInventory().getItemInMainHand();
        final short durability = (short) (newItem.getDurability() + instance.getConfig().getInt("durability"));
        newItem.setDurability(durability);
        if (durability >= newItem.getType().getMaxDurability()) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
            return;
        }
        player.getInventory().setItemInMainHand(newItem);

        setCoolDown(player);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack inHand = player.getInventory().getItemInMainHand();

        if (inHand.getType() == Material.AIR)
            return;

        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        if (!inHand.hasItemMeta())
            return;

        int modelData;
        if (!inHand.getItemMeta().hasCustomModelData())
            return;

        modelData = inHand.getItemMeta().getCustomModelData();

        if (modelData != instance.config.getInt("pickaxe.custom-model-data"))
            return;

        if (event.getBlock().getType() != Material.BEDROCK) {
            player.sendMessage(instance.getPrefix() + instance.translate("messages.not-bedrock"));
            event.setCancelled(true);
        }
        else {
            new BukkitRunnable(){

                public void run() {
                    if (!event.isCancelled())
                        event.getBlock().breakNaturally();
                }
            }.runTaskLater(instance, 1L);

        }

    }

    private int getCoolDownTimeRemaining(Player player) {
        long currTickTime = System.currentTimeMillis() / 50;
        long lastBreakTime = playerCoolDownMap.get(player.getUniqueId());

        return instance.getConfig().getInt("break-delay") - new Long(currTickTime - lastBreakTime).intValue();
    }

    private void setCoolDown(Player player) {
        playerCoolDownMap.put(player.getUniqueId(), System.currentTimeMillis() / 50);
    }

    private boolean playerIsOnCoolDown(Player player) {
        long currTickTime = System.currentTimeMillis() / 50;
        long lastBreakTime = playerCoolDownMap.getOrDefault(player.getUniqueId(), 0L);

        if (playerCoolDownMap.get(player.getUniqueId()) == null)
            return false;

        return currTickTime - lastBreakTime < instance.getConfig().getInt("break-delay");
    }

    private ItemStack initPickaxe() {
        ItemStack bedrockpickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta bedrockpickaxeLabel = bedrockpickaxe.getItemMeta();
        assert bedrockpickaxeLabel != null;
        bedrockpickaxeLabel.setDisplayName(instance.translate("pickaxe.name"));
        List<String> lore = new ArrayList<>();
        for (String line : instance.config.getStringList("pickaxe.lore"))
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        bedrockpickaxeLabel.setLore(lore);
        bedrockpickaxeLabel.setCustomModelData(instance.config.getInt("pickaxe.custom-model-data"));
        bedrockpickaxe.setItemMeta(bedrockpickaxeLabel);
        for (String str : instance.config.getStringList("pickaxe.enchantments")) {
            String[] parts = str.split(":");
            if (parts.length == 1)
                bedrockpickaxe.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(parts[0])), 1);
            else if (parts.length > 1){
                String name = parts[0];
                int level = 1;
                try {
                    level = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e){
                    Bukkit.getConsoleSender().sendMessage("Could not parse level from enchant " + name + ". Using default value.");
                }
                bedrockpickaxe.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(name)), level);
            }
        }
        if (instance.config.getBoolean("pickaxe-recipe"))
            Bukkit.removeRecipe(new NamespacedKey(instance, "bedrock_pickaxe"));
            Bukkit.addRecipe(new CustomRecipe("bedrock_pickaxe", "pickaxe-shaped-recipe", bedrockpickaxe, instance).getShapedRecipe());
        return bedrockpickaxe;
    }

    public ItemStack getItem(){
        this.item = initPickaxe();
        return this.item;
    }
}