package ru.remsoftware.game.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.VariationMessages
import ru.tinkoff.kora.common.Component

@Component
class PlayersDeathListener(
    private val playerService: PlayerService,
    private val database: DataBaseRepository,
    private val logger: Logger,
) : Listener {

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.deathMessage = null
        event.drops.clear()
        event.droppedExp = 0
        val killer = event.entity.killer
        val victim = event.entity.player
        for (potionEffect in victim.activePotionEffects) {
            victim.removePotionEffect(potionEffect.type)
        }
        val ld = victim.lastDamageCause
        if (killer != null) {
            handleStatsOnKill(killer, victim)
        } else {
            val victimData = playerService[victim]!!
            victimData.deaths += 1
            victimData.kit = "default"
            playerService[victim.name] = victimData
            logger.log("${victim.name} умер от $ld")
        }
    }

    private fun handleStatsOnKill(killer: Player, victim: Player) {
        val killerName = killer.name
        val victimName = victim.name
        val killerData = playerService[killerName]!!
        val victimData = playerService[victimName]!!
        val victimMoney = victimData.money
        val moneyForKill: Int = handleMoneyOnKill(victimMoney)

        killerData.currentKills += 1
        killerData.kills += 1
        victimData.currentKills = 0
        victimData.deaths += 1
        victimData.kit = "default"
        victimData.arena = "lobby"
        killerData.money += moneyForKill
        victimData.money -= moneyForKill

        if (victimMoney < 20) {
            VariationMessages.sendMessageWithVariants(victimMoney, victim, "death")
        }
        if (victimMoney > 20) {
            VariationMessages.sendMessageWithVariants(victimMoney, victim, "death")
        }
        VariationMessages.sendMessageWithVariants(moneyForKill, killer, "kill")

        if (victimData.money < 0) victimData.money = 0

        playerService[killerName] = killerData
        playerService[victimName] = victimData
        database.updatePlayer(killerData)
        database.updatePlayer(victimData)
        logger.log("Player ${victim.name} was killed by ${killer.name}")
    }
    private fun handleMoneyOnKill(victimMoney: Int) = if (victimMoney >= 50000) victimMoney / 20 else victimMoney / 10 + 20
}