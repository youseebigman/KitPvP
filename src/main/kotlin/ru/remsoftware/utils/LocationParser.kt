package ru.remsoftware.utils

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import ru.tinkoff.kora.common.Component

@Component
class LocationParser {

    fun locToStr(loc: Location): String = "${loc.world.name}:${loc.blockX}:${loc.blockY}:${loc.blockZ}"

    fun strToLoc(loc: String): Location {
        val parts: List<String> = loc.split(":")
        val world: World = Bukkit.getServer().getWorld(parts[0])
        val x: Double = parts[1].toDouble()
        val y: Double = parts[2].toDouble()
        val z: Double = parts[3].toDouble()
        return Location(world, x, y, z)
    }
}