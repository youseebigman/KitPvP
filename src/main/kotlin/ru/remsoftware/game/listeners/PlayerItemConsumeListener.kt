package ru.remsoftware.game.listeners

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class PlayerItemConsumeListener(
    private val damageManager: PlayerDamageManager,
): Listener {
    @EventHandler
    fun onPlayerConsume(event: PlayerItemConsumeEvent) {
        val item = event.item
        if (item.type == Material.GOLDEN_APPLE) {
            damageManager.hasAbsorption = true
            damageManager.invalidate(event.player.name)
        }
        ChatUtil.sendMessage(event.player, "${item.serialize()}")
    }
}