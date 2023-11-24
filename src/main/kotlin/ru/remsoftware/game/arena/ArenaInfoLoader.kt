package ru.remsoftware.game.arena

import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger

class ArenaInfoLoader(
    private val worldName: String,
    private val dataBaseRepository: DataBaseRepository,
) {
    var arenaLocations: ArenaInfo
        private set

    init {
        var arenaInfo = dataBaseRepository.loadArenaLocations(worldName)
        if (arenaInfo == null) arenaInfo = ArenaInfo(worldName, null)
        this.arenaLocations = arenaInfo
    }
}