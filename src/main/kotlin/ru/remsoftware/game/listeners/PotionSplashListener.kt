package ru.remsoftware.game.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.potion.PotionEffectType
import ru.tinkoff.kora.common.Component

@Component
class PotionSplashListener : Listener {
    @EventHandler
    fun onPotionSplash(event: PotionSplashEvent) {
        val effects = event.potion.effects
        val shooter = event.entity.shooter
        if (shooter is Player) {
            val shooterName = shooter.name
            for (entity in event.affectedEntities) {

                val entityName = entity.name
                if (shooterName.equals(entityName)) {
                    for (effect in effects) {
                        if (effect.type == PotionEffectType.BLINDNESS || effect.type == PotionEffectType.SLOW || effect.type == PotionEffectType.HARM || effect.type == PotionEffectType.WEAKNESS || effect.type == PotionEffectType.CONFUSION || effect.type == PotionEffectType.POISON || effect.type == PotionEffectType.WITHER) {
                            event.setIntensity(shooter, 0.0)
                        }
                    }
                } else if (!entityName.equals(shooterName)) {
                    for (effect in effects) {
                        if (effect.type == PotionEffectType.SPEED || effect.type == PotionEffectType.INCREASE_DAMAGE || effect.type == PotionEffectType.REGENERATION || effect.type == PotionEffectType.JUMP || effect.type == PotionEffectType.DAMAGE_RESISTANCE || effect.type == PotionEffectType.HEAL || effect.type == PotionEffectType.HEALTH_BOOST || effect.type == PotionEffectType.INVISIBILITY || effect.type == PotionEffectType.SATURATION) {
                            event.setIntensity(entity, 0.0)
                        }
                    }
                }
            }
        }
    }
}