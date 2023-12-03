package ru.remsoftware.server

import ru.remsoftware.database.DataBaseRepository

class ServerInfoLoader(
    private val database: DataBaseRepository,
) {
    var spawn: String? = null
        private set

    var globalBooster: Double = 1.0
        private set

    init {
        var serverInfo = database.loadServerInfo()
        if (serverInfo == null) {
            serverInfo = ServerInfoData(spawn, globalBooster)
            database.createServerData(serverInfo)
        }

        this.spawn = serverInfo.spawn
        this.globalBooster = serverInfo.globalBooster
    }

}