package com.bagel.atmospheric.core.other;

import com.bagel.atmospheric.core.Atmospheric;
import com.bagel.atmospheric.core.registry.AtmosphericEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Atmospheric.MODID)
public class AtmosphericEvents {
	
	@SubscribeEvent
	public static void livingHurt(LivingHurtEvent event) {
		LivingEntity entity = event.getEntityLiving();
		
		// GELLED //
		if (event.getEntityLiving().isPotionActive(AtmosphericEffects.GELLED)) {
			if (event.getSource().isFireDamage()) {
				event.setAmount(event.getAmount() * 0.5F);
			}
		}
		
		// RELIEF //
		if (entity.isPotionActive(AtmosphericEffects.RELIEF)) {
			if (entity.isEntityUndead() == false) {
				int amplifier = entity.getActivePotionEffect(AtmosphericEffects.RELIEF).getAmplifier();
				entity.getPersistentData().putInt("PotionHealAmplifier", amplifier);
				entity.getPersistentData().putFloat("IncomingDamage", event.getAmount());
				entity.getPersistentData().putBoolean("Heal", true);
			} else {
				int amplifier = entity.getActivePotionEffect(AtmosphericEffects.RELIEF).getAmplifier();
				if (event.getAmount() >= (amplifier + 1)) {
					event.setAmount(event.getAmount() + (amplifier + 1));
				}
			}
			
		}
		
		// WORSENING //
		if (entity.isPotionActive(AtmosphericEffects.WORSENING)) {
			if (entity.isEntityUndead() == false) {
				int amplifier = entity.getActivePotionEffect(AtmosphericEffects.WORSENING).getAmplifier();
				if (event.getAmount() >= (amplifier + 1)) {
					event.setAmount(event.getAmount() + (amplifier + 1));
				}
			} else {
				int amplifier = entity.getActivePotionEffect(AtmosphericEffects.WORSENING).getAmplifier();
				entity.getPersistentData().putInt("PotionHealAmplifier", amplifier);
				entity.getPersistentData().putFloat("IncomingDamage", event.getAmount());
				entity.getPersistentData().putBoolean("Heal", true);
			}
		}
	}
	
	@SubscribeEvent
	public static void livingTick(LivingUpdateEvent event) {
		
		// RELIEF //
		float damage = event.getEntity().getPersistentData().getFloat("IncomingDamage");
		int amplifierHeal = event.getEntity().getPersistentData().getInt("PotionHealAmplifier");
		boolean heal = event.getEntity().getPersistentData().getBoolean("Heal");
		if (heal == true) {
			if (damage >= (amplifierHeal + 1)) {
				event.getEntityLiving().heal((amplifierHeal + 1));
				event.getEntityLiving().getPersistentData().putBoolean("Heal", false);
			}
		}
	}
}