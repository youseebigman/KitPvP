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
    fun removeMoney(player: Player, amount: Int) {
        val playerData = playerService[player.name]
        if (playerData == null) {
            val offlinePlayerData = playerService.playerDataLoad(player.name)
            val currentMoney = offlinePlayerData.money
            val newMoney = currentMoney - amount
            database.updateMoney(player.name, newMoney)
        } else {
            val currentMoney = playerData.money
            val newMoney = currentMoney - amount
            playerData.money = newMoney
            playerService[player.name] = playerData
            val remainder = amount % 10
            if (remainder == 0 || remainder > 4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы потратили &a&l$amount &fмонет")
            }
            if (remainder == 1) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы потратили &a&l$amount &fмонету")
            }
            if (remainder in 2..4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы потратили &a&l$amount &fмонеты")
            }
        }
    }
    fun addMoneyWithBoost(amount: Int, player: Player) {
        val playerData = playerService[player.name]
        if (playerData == null) {
            val offlinePlayerData = playerService.playerDataLoad(player.name)
            val currentMoney = offlinePlayerData.money
            val newMoney = currentMoney + amount
            database.updateMoney(player.name, newMoney)
        } else {
            val currentMoney = playerData.money
            val booster = playerData.localBooster
            val bMoney = boostMoney(amount, booster)
            val newMoney = currentMoney + bMoney
            playerData.money = newMoney
            playerService[player.name] = playerData
            val remainder = bMoney % 10
            if (remainder == 0 || remainder > 4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы получили &a&l$bMoney &fмонет")
            }
            if (remainder == 1) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы получили &a&l$bMoney &fмонету")
            }
            if (remainder in 2..4) {
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&f Вы получили &a&l$bMoney &fмонеты")
            }
        }
    }
    fun addMoney(amount: Int, player: Player) {
        val playerData = playerService[player.name]
        if (playerData == null) {
            val offlinePlayerData = playerService.playerDataLoad(player.name)
            val currentMoney = offlinePlayerData.money
            val newMoney = currentMoney + amount
            database.updateMoney(player.name, newMoney)
        } else {
            val currentMoney = playerData.money
            val newMoney = currentMoney + amount
            playerData.money = newMoney
            playerService[player.name] = playerData
        }
    }

    fun boostMoney(amount: Int, booster: Double) : Int {
        val boostMoney = amount * booster
        return boostMoney.toInt()
    }


}