package ru.remsoftware.game.player

import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffectType
import org.bukkit.projectiles.ProjectileSource
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class PlayerCombatManager(
    private val playerAbsorptionService: PlayerAbsorptionService,
) : Listener {
    val combatMap = hashMapOf<String, Long>()
    val lastDamagerMap = hashMapOf<String, String>()
    fun getCombatDuration(name: String) = combatMap[name]
    fun getLastDamager(name: String) = lastDamagerMap[name]

    fun invalidateCombat(name: String) = combatMap.remove(name)
    fun invalidateLastDamager(name: String) = lastDamagerMap.remove(name)
    fun setCombat(name: String, kd: Long) {
        combatMap[name] = kd
    }

    fun setLastDamager(name: String, damagerName: String) {
        lastDamagerMap[name] = damagerName
    }

    @EventHandler
    fun onEntityDamageEntity(event: EntityDamageByEntityEvent) {
        val player = event.entity
        val playerName = player.name
        var damager = event.damager
        var damagerName = damager.name
        if (damager is Arrow) {
            val shooter = damager.shooter
            if (shooter is Player) {
                damager = shooter
                damagerName = shooter.name
            }
        }
        if (!damagerName.equals(playerName)) {
            val combatDuration = System.currentTimeMillis() + 8000
            if (player is Player) {
                val hasDuration = isCombatPlayer(playerName)
                if (!hasDuration) {
                    ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&e Вы начали бой с игроком! Не получайте и не наносите урон 8 секунд чтобы телепортироваться или выйти из игры!")
                    setLastDamager(playerName, damagerName)
                    setCombat(playerName, combatDuration)
                    GlobalTaskContext.everyAsync(1, 20) {
                        val playerCombatDuration = getCombatDuration(playerName)
                        if (System.currentTimeMillis() >= playerCombatDuration!!) {
                            invalidateCombat(playerName)
                            invalidateLastDamager(playerName)
                            it.cancel()
                        }
                    }
                } else {
                    setCombat(playerName, combatDuration)
                }
                if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                    handleAbsorptionHP(player, event.damage)
                }
            }
            if (damager is Player) {
                val hasDuration = isCombatPlayer(damagerName)
                if (!hasDuration) {
                    ChatUtil.sendMessage(damager, "&8[&b&lKit&4&lPvP&8]&e Вы начали бой с игроком! Не получайте и не наносите урон 8 секунд чтобы телепортироваться или выйти из игры!")
                    setLastDamager(damagerName, playerName)
                    setCombat(damagerName, combatDuration)
                    GlobalTaskContext.everyAsync(1, 20) {
                        val playerCombatDuration = getCombatDuration(damagerName)!!
                        if (System.currentTimeMillis() >= playerCombatDuration) {
                            invalidateCombat(damagerName)
                            invalidateLastDamager(damagerName)
                            it.cancel()
                        }
                    }
                } else {
                    setCombat(damagerName, combatDuration)
                }
            }
        }
    }

    fun isCombatPlayer(name: String): Boolean = getCombatDuration(name) != null

    fun handleAbsorptionHP(player: Player, damage: Double) {
        playerAbsorptionService.increase(player.name, damage)
        val currentDmg = playerAbsorptionService[player]!!
        val pet = player.getPotionEffect(PotionEffectType.ABSORPTION)
        val absHP = (pet.amplifier + 1) * 4
        println("current = $currentDmg")
        if (currentDmg > absHP) {
            player.removePotionEffect(PotionEffectType.ABSORPTION)
            playerAbsorptionService.hasAbsorption = false
            playerAbsorptionService.invalidate(player.name)
        } else {
            if (currentDmg >= 1) {
                if (currentDmg % 4 == 0) {
                    val newAbsLevel = ((absHP - currentDmg) / 4)
                    playerAbsorptionService.newAmplifier = newAbsLevel
                } else if (currentDmg % 4 >= 2) {
                    val newAbsLevel = ((absHP - currentDmg) / 4)
                    playerAbsorptionService.newAmplifier = newAbsLevel
                } else if (currentDmg % 4 == 1) {
                    val newAbsLevel = ((absHP - currentDmg) / 4) + 1
                    playerAbsorptionService.newAmplifier = newAbsLevel
                }
            } else {
                playerAbsorptionService.newAmplifier = pet.amplifier
            }
        }
    }
}