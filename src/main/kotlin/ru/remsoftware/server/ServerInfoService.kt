package ru.remsoftware.server

import org.bukkit.World
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.parser.LocationParser
import ru.remsoftware.utils.Logger
import ru.tinkoff.kora.common.Component

@Component
class ServerInfoService(
    private val logger: Logger,
) {
    var serverInfo: ServerInfo? = null

    fun loadInfo(world: World, database: DataBaseRepository, locationParser: LocationParser) : ServerInfo {
        val infoLoader = ServerInfoLoader(world, database)
        logger.log("Загрузка данных сервера")
        return if (infoLoader.spawn == null) {
            ServerInfo(null, infoLoader.globalBooster)
        } else {
            ServerInfo(locationParser.strToLoc(infoLoader.spawn!!), infoLoader.globalBooster)
        }

    }

}