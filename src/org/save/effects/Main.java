package org.save.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.base.Objects;

public class Main extends JavaPlugin implements Listener{   

	Map<String, Object> effectsToConfig = new LinkedHashMap<String, Object>();	
	FileConfiguration config = getConfig();
	
	@Override
	public void onEnable() {
		effectsToConfig.put("SPEED", true);
		effectsToConfig.put("JUMP", true);
		effectsToConfig.put("REGENERATION", true);
		effectsToConfig.put("HEAL", true); //instant_health
		effectsToConfig.put("NIGHT_VISION", true);
		effectsToConfig.put("ABSORPTION", true);
		effectsToConfig.put("CONDUIT_POWER", true);
		effectsToConfig.put("DAMAGE_RESISTANCE", true);
		effectsToConfig.put("FAST_DIGGING", true); //haste
		effectsToConfig.put("DOLPHINS_GRACE", true);
		effectsToConfig.put("FIRE_RESISTANCE", true);
		effectsToConfig.put("GLOWING", true);
		effectsToConfig.put("HEALTH_BOOST", true);
		effectsToConfig.put("HERO_OF_THE_VILLAGE", true);
		effectsToConfig.put("INVISIBILITY", true);
		effectsToConfig.put("LEVITATION", true);
		effectsToConfig.put("LUCK", true);
		effectsToConfig.put("INCREASE_DAMAGE", true); //strength
		effectsToConfig.put("SATURATION", true);
		effectsToConfig.put("SLOW_FALLING", true);
		effectsToConfig.put("WATER_BREATHING", true);
		
		effectsToConfig.put("BAD_OMEN", false);
		effectsToConfig.put("SLOW_DIGGING", false); //mining fatigue
		effectsToConfig.put("SLOW", false);
		effectsToConfig.put("HARM", false); //instant_damage
		effectsToConfig.put("HUNGER", false);
		effectsToConfig.put("BLINDNESS", false);
		effectsToConfig.put("CONFUSION", false);
		effectsToConfig.put("POISON", false);
		effectsToConfig.put("UNLUCK", false);
		effectsToConfig.put("WEAKNESS", false);
		effectsToConfig.put("WITHER", false);
        
        config.addDefaults(effectsToConfig);
        config.options().copyDefaults(true);
        saveConfig();
        
		getServer().getPluginManager().registerEvents(this, this);
		
	}

	Map<String, Object> effectsFromConf = (HashMap<String, Object>) config.getValues(true);

	Map<Player, List<PotionEffect>> savedEffects = new HashMap<Player, List<PotionEffect>>();
	Map<String, List<PotionEffect>> longSavedEffects = new HashMap<String, List<PotionEffect>>();
	
	@EventHandler
	public void onDeath(PlayerDeathEvent death) {
		Player playerDeath = death.getEntity();
		List<PotionEffect> tempSavedEffects = new ArrayList<PotionEffect>();
		
		if(playerDeath.hasPermission("saveeffects")) 
		{
		for (Map.Entry<String, Object> entry : effectsFromConf.entrySet()){		
		PotionEffect getEffect = playerDeath.getPotionEffect(PotionEffectType.getByName(entry.getKey()));
			if((boolean) entry.getValue() == true &  getEffect != null) 
			{
				tempSavedEffects.add(getEffect);
				savedEffects.put(playerDeath, tempSavedEffects);
			}
		}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent quit) {
		Player quitPlayer = quit.getPlayer();
		if(quitPlayer.hasPermission("saveeffects")) 
		{
		for (Map.Entry<Player, List<PotionEffect>> entry : savedEffects.entrySet()) {
			if(entry.getKey() == quitPlayer & quitPlayer.isDead()) {
				longSavedEffects.put(entry.getKey().getDisplayName(), entry.getValue());
				savedEffects.remove(entry.getKey());
				}
			}
		}
	}
	
	@EventHandler
	public void onResp(PlayerRespawnEvent resp){
		Player playerResp = resp.getPlayer();
		if(playerResp.hasPermission("saveeffects")) 
		{
		Bukkit.getScheduler().runTaskLater(this, () -> {
			
			if(longSavedEffects.containsKey(playerResp.getDisplayName())) {
			playerResp.sendMessage("Effects restored from long list");
				
			for (Map.Entry<String, List<PotionEffect>> entry : longSavedEffects.entrySet()) 
				if(Objects.equal(entry.getKey(), playerResp.getDisplayName())) {
				for(PotionEffect effect: entry.getValue())
					playerResp.addPotionEffect(effect);
				longSavedEffects.remove(entry.getKey());
				}
			}
			}, 30);
		}
		if(playerResp.hasPermission("saveeffects")) 
		{
		Bukkit.getScheduler().runTaskLater(this, () -> {
		
			if(savedEffects.containsKey(playerResp)) {
			playerResp.sendMessage("Effects restored");
			for (Map.Entry<Player, List<PotionEffect>> entry : savedEffects.entrySet()) 
				if(entry.getKey() == playerResp) {
				for(PotionEffect effect: entry.getValue())
					playerResp.addPotionEffect(effect);
				}
			}
			}, 10);
		}
		if(playerResp.hasPermission("saveeffects")) 
		{
		Bukkit.getScheduler().runTaskLater(this, () -> {
			for (Map.Entry<Player, List<PotionEffect>> entry : savedEffects.entrySet()) 
				if(entry.getKey() == playerResp) {
					savedEffects.remove(entry.getKey());
				}
		}, 11);
		}
		
	}
	
	
}

