package com.akabex86.features.lootshare;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.akabex86.features.FeaturePlugin;
import com.akabex86.features.lootshare.listeners.DamageEntity;
import com.akabex86.features.lootshare.listeners.EntityDeath;
import com.akabex86.features.lootshare.listeners.PickupItemandXP;
import com.akabex86.main.Main;

public class LootShare extends FeaturePlugin{
	

	public static final NamespacedKey keyTag = new NamespacedKey(JavaPlugin.getPlugin(Main.class), "lootshare:owner");
	private static EntityDamageRegistry entityDamageRegistry = new EntityDamageRegistry();
	public static final double SHARELOOTPERCENTAGE = 0.6;
	
	public LootShare(Plugin plugin) {
		super(plugin);
	}

	@Override
	public void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new DamageEntity(), getPlugin());
		Bukkit.getPluginManager().registerEvents(new EntityDeath(), getPlugin());
		Bukkit.getPluginManager().registerEvents(new PickupItemandXP(), getPlugin());
	}
	
	public static EntityDamageRegistry getEntityDamageRegistry() {
		return entityDamageRegistry;
	}
	
	public static boolean isValidEntity(Entity entity) {
		return !((entity instanceof Player) || (entity instanceof Boss));
	}
	
	public static void spawnLoot(ItemStack item, Location location, Player owner) {
		World world = location.getWorld();
		world.dropItemNaturally(location, item, droppedItem -> {
			PersistentDataContainer data = droppedItem.getPersistentDataContainer();
			data.set(keyTag, PersistentDataType.STRING, owner.getUniqueId().toString());
		});
	}
}