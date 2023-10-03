package ru.remsoftware.game.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffectType
import ru.tinkoff.kora.common.Component

@Component
class PlayerDamageManager : Listener {
    var newAmplifier: Int? = null
    var hasAbsorption: Boolean? = null
    val damageToAbsorption = hashMapOf<String, Int>()

    operator fun get(name: String) = damageToAbsorption[name]

    operator fun get(player: Player) = get(player.name)

    fun invalidate(name: String) = damageToAbsorption.remove(name)

    fun increase(name: String, damage: Double) {
        val currentDmg = get(name)
        if (currentDmg != null) {
            damageToAbsorption[name] = (currentDmg + damage).toInt()
        } else {
            damageToAbsorption[name] = damage.toInt()
        }
    }

    @EventHandler
    fun onEntityDamageEntity(event: EntityDamageByEntityEvent) {
        val player = event.entity
        if (player is Player) {
            handleAbsorptionHP(player, event.damage)
        }
    }

    @EventHandler
    fun onPlayerGetDamage(event: EntityDamageEvent) {
        val player = event.entity
        if (player is Player) {
            handleAbsorptionHP(player, event.damage)
        }
    }
    

    fun handleAbsorptionHP(player: Player, damage: Double) {
        if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
            increase(player.name, damage)

            val currentDmg = get(player)!!
            val pet = player.getPotionEffect(PotionEffectType.ABSORPTION)
            val absHP = (pet.amplifier + 1) * 4
            if (currentDmg > absHP) {
                player.removePotionEffect(PotionEffectType.ABSORPTION)
                hasAbsorption = false
                invalidate(player.name)
            } else {
                if (currentDmg >= 1) {
                    if (currentDmg % 4 == 0) {
                        val newAbsLevel = ((absHP - currentDmg) / 4)
                        newAmplifier = newAbsLevel
                    } else if (currentDmg % 4 >= 2) {
                        val newAbsLevel = ((absHP - currentDmg) / 4)
                        newAmplifier = newAbsLevel
                    } else if (currentDmg % 4 == 1) {
                        val newAbsLevel = ((absHP - currentDmg) / 4) + 1
                        newAmplifier = newAbsLevel
                    }
                } else {
                    newAmplifier = pet.amplifier
                }
            }
        }
    }
}