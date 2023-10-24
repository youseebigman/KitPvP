package ru.remsoftware.game.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.event.world.WorldUnloadEvent
import ru.remsoftware.game.arena.ArenaService
import ru.tinkoff.kora.common.Component

@Component
class WorldListener(
    private val arenaService: ArenaService,
) : Listener {

    @EventHandler
    fun onLoadWorld(event: WorldLoadEvent) {
        arenaService.loadArenaInfo(event.world.name)
    }
    @EventHandler
    fun onWorldUnload(event: WorldUnloadEvent) {
        arenaService.update(event.world.name)
    }
}