package ru.remsoftware.server

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.parser.LocationParser
import ru.tinkoff.kora.common.Component

@Component
class ServerInfoService(
    private val logger: Logger,
) {
    var serverInfo: ServerInfo? = null
    var spawn: Location? = null
    private var bossBar = Bukkit.getServer().createBossBar("", BarColor.PURPLE, BarStyle.SOLID)
    var killStreakBossBar = Pair<String?, BossBar>(null, bossBar)

    fun loadInfo(database: DataBaseRepository, locationParser: LocationParser) {
        val infoLoader = ServerInfoLoader(database)
        logger.log("Загрузка данных сервера")
        if (infoLoader.spawn == null) {
            serverInfo = ServerInfo(null, infoLoader.globalBooster)
        } else {
            serverInfo = ServerInfo(locationParser.strToLoc(infoLoader.spawn!!), infoLoader.globalBooster)
            spawn = locationParser.strToLoc(infoLoader.spawn!!)
        }
    }
}