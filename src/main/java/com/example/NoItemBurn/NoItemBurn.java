package com.example.NoItemBurn;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Item;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.HashMap;

public class NoItemBurn extends JavaPlugin implements Listener {
    private HashMap<UUID, ItemStack> burningItems = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("NoItemBurn Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("NoItemBurn Plugin Disabled!");
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity().getType() == EntityType.DROPPED_ITEM) {
            storeAndProtectItem((Item) event.getEntity(), event);
        }
    }

    private void storeAndProtectItem(Item item, EntityCombustEvent event) {
        burningItems.put(item.getUniqueId(), item.getItemStack().clone());
        event.setCancelled(true);
        restoreItemIfRemoved(item);
    }

    private void restoreItemIfRemoved(Item item) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!item.isValid() &&
                    burningItems.containsKey(item.getUniqueId())
                ) {
                    var itemStack = burningItems.get(item.getUniqueId());
                    item.getWorld().dropItem(item.getLocation(), itemStack);
                    burningItems.remove(item.getUniqueId());
                }
            }
        }.runTaskLater(this, 20L);
    }
}