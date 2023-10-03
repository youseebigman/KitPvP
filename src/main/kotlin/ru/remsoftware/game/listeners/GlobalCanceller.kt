package ru.remsoftware.game.listeners

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import ru.remsoftware.game.signs.SignService
import ru.tinkoff.kora.common.Component

@Component
class GlobalCanceller(
    private val signService: SignService,
) : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block
        val signWorkers = signService.getWorkers()
        if (event.player.isOp) {
            if (block.type.equals(Material.SIGN) || block.type.equals(Material.SIGN_POST) || block.type.equals(Material.WALL_SIGN)) {
                if (!signWorkers.contains(event.player.name)) {
                    event.isCancelled = true
                }
            }
            if (signWorkers.contains(event.player.name)) {
                event.isCancelled = true
            }
        } else {
            event.isCancelled = true
        }
    }
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!event.player.isOp) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        if (event.spawnReason.equals(CreatureSpawnEvent.SpawnReason.NATURAL) || event.spawnReason.equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            event.isCancelled = true
        }
    }
}