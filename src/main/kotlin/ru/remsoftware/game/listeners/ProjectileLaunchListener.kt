package ru.remsoftware.game.listeners

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent
import ru.remsoftware.game.potions.PotionService
import ru.remsoftware.utils.VariationMessages
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.item.name
import ru.starfarm.core.util.time.CooldownUtil
import ru.tinkoff.kora.common.Component
import java.util.concurrent.TimeUnit

@Component
class ProjectileLaunchListener(
    private val potionService: PotionService,
) : Listener {
    private val potionNameList = potionService.getAllPotionsName()

    @EventHandler
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        val projectile = event.entity
        val player = projectile.shooter
        if (projectile is ThrownPotion && player is Player) {
            val item = projectile.item
            val itemName = item.name!!.replace("§", "&")
            if (potionNameList.contains(itemName)) {
                val customPotion = potionService[itemName]
                if (CooldownUtil.has(itemName, player)) {
                    event.isCancelled = true
                    val timeLeft = TimeUnit.MILLISECONDS.toSeconds(CooldownUtil.get(itemName, player))
                    VariationMessages.sendMessageWithVariants(timeLeft.toInt(), player, "potion_wait", null, null)
                    player.playSound(player.eyeLocation, Sound.BLOCK_NOTE_BASS, 1.0f, 1.0f)
                    GlobalTaskContext.asyncAfter(3) {
                        player.inventory.addItem(item)
                        it.cancel()
                    }
                } else {
                    CooldownUtil.put(itemName, player, customPotion!!.cooldown)
                    GlobalTaskContext.asyncAfter(3) {
                        player.inventory.addItem(item)
                        it.cancel()
                    }
                }
            }
        }
    }
}

