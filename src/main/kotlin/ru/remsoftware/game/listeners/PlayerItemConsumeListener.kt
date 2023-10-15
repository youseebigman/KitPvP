package ru.remsoftware.game.listeners

import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import ru.remsoftware.game.player.PlayerDamageService
import ru.remsoftware.game.potions.PotionData
import ru.remsoftware.game.potions.PotionService
import ru.remsoftware.utils.VariationMessages
import ru.remsoftware.utils.parser.PotionEffectParser
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.name
import ru.starfarm.core.util.time.Cooldown
import ru.starfarm.core.util.time.CooldownUtil
import ru.tinkoff.kora.common.Component
import java.util.concurrent.TimeUnit

@Component
class PlayerItemConsumeListener(
    private val damageService: PlayerDamageService,
    private val potionService: PotionService,
    private val potionEffectParser: PotionEffectParser,
) : Listener {

    @EventHandler
    fun onPlayerConsume(event: PlayerItemConsumeEvent) {
        val item = event.item
        val player = event.player
        val itemName = ChatColor.stripColor(item.name)
        println(itemName)
        if (itemName != null) {
            val customPotionsNameList = getAllPotionsName(potionService.all())
            println(customPotionsNameList)
            if (customPotionsNameList.contains(itemName)) {
                println("contains")
                val customPotion = potionService[itemName]
                if (customPotion != null) {
                    if (CooldownUtil.has(itemName, player)) {
                        event.isCancelled = true
                        val timeLeft = TimeUnit.MILLISECONDS.toSeconds(CooldownUtil.get(itemName, player))
                        VariationMessages.sendMessageWithVariants(timeLeft.toInt(), player, "potion_wait")
                        player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
                    } else {
                        event.isCancelled = true
                        potionEffectParser.effectsInPotionToPlayer(item, player)
                        CooldownUtil.put(itemName, player, customPotion.cooldown)
                    }
                } else {
                    println("Potion not created!")
                }
            }
        }

        if (item.type == Material.GOLDEN_APPLE) {
            event.isCancelled = true
            damageService.hasAbsorption = true
            damageService.invalidate(event.player.name)
        }
    }

    fun getAllPotionsName(potions: MutableCollection<PotionData>): List<String> {
        val potionsNameList = arrayListOf<String>()
        potions.forEach {
            potionsNameList.add(it.name)
        }
        return potionsNameList
    }
}