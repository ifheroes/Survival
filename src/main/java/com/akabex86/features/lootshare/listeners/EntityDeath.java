package com.akabex86.features.lootshare.listeners;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.akabex86.features.lootshare.LootShare;

public class EntityDeath implements Listener{

	@EventHandler
	public void entityDeath(EntityDeathEvent event) {
		LivingEntity killedEntity = event.getEntity();
		if(!LootShare.isValidEntity(killedEntity)) return;
		if(killedEntity.getKiller() == null) return;
		
		Comparator<Map.Entry<Player, Double>> comparator = Map.Entry.<Player, Double>comparingByValue().reversed();
		LinkedHashMap<Player, Double> damageComposit = LootShare.getEntityDamageRegistry().getDamageComposite(killedEntity, comparator);
		
		double maxDamage = damageComposit.values().stream().findFirst().orElse(0D); if(maxDamage == 0D) return;
		
		damageComposit.entrySet().stream()
						.filter(entry -> entry.getValue() >= LootShare.SHARELOOTPERCENTAGE * maxDamage)
						.map(m -> m.getKey())
						.forEach(player -> {
							event.getDrops().forEach(drop -> LootShare.spawnLoot(drop, killedEntity.getLocation(), player));
							player.giveExp(event.getDroppedExp());
							player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
						});

		event.getDrops().clear();
		event.setDroppedExp(0);
		LootShare.getEntityDamageRegistry().removeEntity(killedEntity);
	}
}
