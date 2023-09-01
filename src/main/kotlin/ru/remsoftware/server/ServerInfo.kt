package ru.remsoftware.server

import org.bukkit.Location

data class ServerInfo(
    var spawn: Location,
    var globalBooster: Double,
) {

}