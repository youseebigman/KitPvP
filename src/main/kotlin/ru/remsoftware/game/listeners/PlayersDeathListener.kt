package ru.remsoftware.game.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.VariationMessages
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class PlayersDeathListener(
    private val playerService: PlayerService,
    private val database: DataBaseRepository,
    private val logger: Logger,
    private val moneyManager: MoneyManager,
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
            val moneyForKill = moneyManager.handleMoneyOnKill(victimData.money)
            victimData.currentKills = 0
            victimData.deaths += 1
            victimData.arena = "lobby"
            victimData.kit = "default"
            moneyManager.removeMoneyBecauseDeath(victim, moneyForKill)
            playerService[victim.name] = victimData
            ChatUtil.sendMessage(victim, "&8[&b&lKit&4&lPvP&8]&f Вы потеряли &a$moneyForKill &fмонет из-за смерти")
            logger.log("${victim.name} умер от $ld")
        }
    }

    private fun handleStatsOnKill(killer: Player, victim: Player) {
        val killerName = killer.name
        val victimName = victim.name
        val killerData = playerService[killerName]!!
        val victimData = playerService[victimName]!!
        val victimMoney = victimData.money
        val moneyForKill: Int = moneyManager.handleMoneyOnKill(victimData.money)

        if (victimName == killerName) {
            victimData.deaths += 1
            victimData.currentKills = 0
            victimData.kit = "default"
            moneyManager.removeMoneyBecauseDeath(victim, moneyForKill)
            playerService[victim.name] = victimData
            ChatUtil.sendMessage(victim, "&8[&b&lKit&4&lPvP&8]&f Вы потеряли &a$moneyForKill &fмонет из-за смерти")
            logger.log("${victim.name} умер от себя")
        } else {
            killerData.currentKills += 1
            killerData.kills += 1
            victimData.currentKills = 0
            victimData.deaths += 1
            victimData.kit = "default"
            victimData.arena = "lobby"
            moneyManager.addMoney(moneyForKill, killer)

            if (victimMoney <= 20) {
                victimData.money = 0
                VariationMessages.sendMessageWithVariants(victimMoney, null, "death", victim, killer)
            } else {
                victimData.money -= moneyForKill
                VariationMessages.sendMessageWithVariants(moneyForKill, null, "death", victim, killer)
            }
            VariationMessages.sendMessageWithVariants(moneyForKill, null, "kill", victim, killer)
            if (victimData.money < 0) victimData.money = 0
            playerService[killerName] = killerData
            playerService[victimName] = victimData
            database.updatePlayer(killerData)
            database.updatePlayer(victimData)
            logger.log("Player ${victim.name} was killed by ${killer.name}")
        }
    }

}