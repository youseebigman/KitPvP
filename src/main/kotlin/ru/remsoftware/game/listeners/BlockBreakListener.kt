package ru.remsoftware.game.listeners

import org.bukkit.event.block.BlockBreakEvent
import ru.remsoftware.game.signs.SignService
import ru.tinkoff.kora.common.Component

@Component
class BlockBreakListener(
    private val signService: SignService,
) {
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val signWorkers = signService.getWorkers()
        if (!player.isOp) {
            event.isCancelled = true
            event.isDropItems = false
        }
        else if (signWorkers.contains(player.name)) {
            event.isCancelled = true
        }
    }
}