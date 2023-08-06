package ru.remsoftware.game.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import ru.tinkoff.kora.common.Component

@Component
class MobSpawnListener : Listener {
    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        if (event.spawnReason.equals(CreatureSpawnEvent.SpawnReason.NATURAL) || event.spawnReason.equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            event.isCancelled = true
        }
    }
}