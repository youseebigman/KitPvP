package ru.remsoftware.game.arena

import org.bukkit.Location
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.parser.ArenaLocationParser
import ru.starfarm.core.util.bukkit.LocationUtil
import ru.starfarm.core.util.math.Cuboid
import ru.tinkoff.kora.common.Component

@Component
class ArenaService(
    private val dataBaseRepository: DataBaseRepository,
    private val arenaLocationParser: ArenaLocationParser,
    private val logger: Logger,
) {
    private val arenaLocations = hashMapOf<String, ArrayList<Location>>()

    val SPAWN_ARENA = Cuboid.atLocations(
        LocationUtil.fromString("lobby 13 110 -12"),
        LocationUtil.fromString("lobby -13 99 13")
    )
    operator fun get(worldName: String) = arenaLocations[worldName]

    fun update(worldName: String) {
        val list = arenaLocations[worldName]
        if (list != null) {
            val jsonList = arenaLocationParser.locationListToJsonArray(list)
            dataBaseRepository.updateArenaLocations(worldName, jsonList)
            logger.log("Update spawn points for $worldName")
        }
    }

    fun addSpawnPoint(worldName: String, location: Location) {
        val currentList = get(worldName)
        if (currentList == null) {
            val newList = arrayListOf(location)
            arenaLocations[worldName] = newList
            dataBaseRepository.createArenaLocation(worldName, arenaLocationParser.locationListToJsonArray(newList))
        } else {
            currentList.add(location)
            arenaLocations[worldName] = currentList
        }
    }

    fun loadArenaInfo(worldName: String) {
        val arenaInfo = ArenaInfoLoader(worldName, dataBaseRepository).arenaLocations
        val spawnPoints = arenaInfo.spawnPoints
        if (spawnPoints != null) {
            val locationList = arenaLocationParser.jsonArrayToLocationList(spawnPoints)
            arenaLocations[worldName] = locationList
            logger.log("Spawn locations for $worldName have been loaded")
        } else {
            logger.log("Spawn locations for $worldName not found")
        }
    }


}