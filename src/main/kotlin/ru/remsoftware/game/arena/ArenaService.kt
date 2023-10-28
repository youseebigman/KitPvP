package ru.remsoftware.game.arena

import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.parser.ArenaLocationParser
import ru.tinkoff.kora.common.Component

@Component
class ArenaService(
    private val dataBaseRepository: DataBaseRepository,
    private val arenaLocationParser: ArenaLocationParser,
    private val logger: Logger,
    private val playerService: PlayerService,
) {
    private val arenaLocations = hashMapOf<String, ArrayList<Location>>()

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
        val arenaInfo = ArenaInfoLoader(worldName, dataBaseRepository, logger).arenaLocations
        val spawnPoints = arenaInfo.spawnPoints
        if (spawnPoints != null) {
            val locationList = arenaLocationParser.jsonArrayToLocationList(spawnPoints)
            arenaLocations[worldName] = locationList
        } else {
            logger.log("Spawn locations for $worldName not found")
        }
    }

    fun teleportOnRandomSpawnPoints(worldName: String, player: Player) {
        val kitPlayer = playerService[player.name]!!
        val locationList = get(worldName)
        if (locationList != null) {
            val location = locationList.random()
            player.teleport(location)
            kitPlayer.arena = worldName
            playerService[player.name] = kitPlayer
            player.playSound(player.eyeLocation, Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 1.0f)
        }
    }
}