package ru.remsoftware.game.money

import org.bukkit.entity.Player
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.player.PlayerService
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class MoneyManager(
    private val database: DataBaseRepository,
    private val playerService: PlayerService,
) {

    fun addMoney(name: String, amount: Int, player: Player) {
        val playerData = playerService[name]
        if (playerData == null) {
            val offlinePlayerData = playerService.playerDataLoad(name)
            val currentMoney = offlinePlayerData.money
            val newMoney = currentMoney + amount
            database.updateMoney(name, newMoney)
        } else {
            val currentMoney = playerData.money
            val booster = playerData.localBooster
            val bMoney = boostMoney(amount, booster)
            val newMoney = currentMoney + bMoney
            playerData.money = newMoney
            playerService[name] = playerData
            val remainder = bMoney % 10
            if (remainder == 0 || remainder > 4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы получили $bMoney монет")
            }
            if (remainder == 1) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы получили $bMoney монету")
            }
            if (remainder in 2..4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Вы получили $bMoney монеты")
            }
        }
    }

    fun boostMoney(amount: Int, booster: Double) : Int {
        val boostMoney = amount * booster
        return boostMoney.toInt()
    }


}