package ru.remsoftware.game.potions

import org.bukkit.entity.Player
import ru.remsoftware.database.DataBaseRepository
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component
import java.util.concurrent.TimeUnit

@Component
class PotionManager (
    private val potionService: PotionService,
    private val dataBaseRepository: DataBaseRepository,
) {
    fun createPotion(name: String, cooldown: Long, effects: String, player: Player) {
        val potionData = PotionData(name, TimeUnit.SECONDS.toMillis(cooldown), effects)
        potionService.createPotion(potionData, dataBaseRepository)
        ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&a&l Вы успешно создали $name")
    }
    fun updatePotion(name: String, cooldown: Long, effects: String, player: Player) {
        val potionData = PotionData(name, TimeUnit.SECONDS.toMillis(cooldown), effects)
        potionService.updatePotion(potionData, dataBaseRepository)
        ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&a&l Вы успешно обновили $name")
    }
}