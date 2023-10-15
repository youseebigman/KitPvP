package ru.remsoftware.game.player

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffectType
import ru.tinkoff.kora.common.Component

@Component
class PlayerDamageManager(
    private val playerService: PlayerService,
    private val playerDamageService: PlayerDamageService,
) : Listener {
    @EventHandler
    fun onEntityDamageEntity(event: EntityDamageByEntityEvent) {
        val player = event.entity
        if (player is Player) {
            if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                handleAbsorptionHP(player, event.damage)
            }
        }
    }

    @EventHandler
    fun onPlayerGetDamage(event: EntityDamageEvent) {
        val player = event.entity
        val cause = event.cause
        if (player is Player) {
            val kitPlayer = playerService[player]!!
            if (kitPlayer.kit == "Ангел") {
                if (cause == (EntityDamageEvent.DamageCause.FALL)) {
                    event.isCancelled = true
                }
            }
            if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) {  
                handleAbsorptionHP(player, event.damage)
            }
        }
    }

    fun handleAbsorptionHP(player: Player, damage: Double) {
        playerDamageService.increase(player.name, damage)
        val currentDmg = playerDamageService[player]!!
        val pet = player.getPotionEffect(PotionEffectType.ABSORPTION)
        val absHP = (pet.amplifier + 1) * 4
        println("current = $currentDmg")
        if (currentDmg > absHP) {
            player.removePotionEffect(PotionEffectType.ABSORPTION)
            playerDamageService.hasAbsorption = false
            playerDamageService.invalidate(player.name)
        } else {
            if (currentDmg >= 1) {
                if (currentDmg % 4 == 0) {
                    val newAbsLevel = ((absHP - currentDmg) / 4)
                    playerDamageService.newAmplifier = newAbsLevel
                } else if (currentDmg % 4 >= 2) {
                    val newAbsLevel = ((absHP - currentDmg) / 4)
                    playerDamageService.newAmplifier = newAbsLevel
                } else if (currentDmg % 4 == 1) {
                    val newAbsLevel = ((absHP - currentDmg) / 4) + 1
                    playerDamageService.newAmplifier = newAbsLevel
                }
            } else {
                playerDamageService.newAmplifier = pet.amplifier
            }
        }
    }
}