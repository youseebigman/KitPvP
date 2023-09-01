package ru.remsoftware.server

import org.bukkit.World
import ru.remsoftware.database.DataBaseRepository

class ServerInfoLoader(
    private val world: World,
    private val database: DataBaseRepository,

) {
    var spawn: String = "${world.name}:1:1:1"
        private set

    var globalBooster: Double = 1.0
        private set

    init {
        var serverInfo = database.loadServerInfo(world.name)
        if (serverInfo == null) {
            serverInfo = ServerInfoData(spawn, globalBooster)
            database.createServerData(world.name, serverInfo)
        }

        this.spawn = serverInfo.spawn
        this.globalBooster = serverInfo.globalBooster
    }

}