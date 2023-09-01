package ru.remsoftware.utils.parser

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import ru.tinkoff.kora.common.Component

@Component
class LocationParser {

    fun locToStr(location: Location) = location.run { "${world.name} $x $y $z $yaw $pitch" }

    fun strToLoc(loc: String): Location {
        val args: List<String> = loc.split(" ")
        require(args.size >= 4) { "not parameters" }
        val world: World = Bukkit.getServer().getWorld(args[0])
        val x = args[1].toDouble()
        val y = args[2].toDouble()
        val z = args[3].toDouble()
        val yaw = if (args.size >= 6) args[4].toFloat() else 0f
        val pitch = if (args.size >= 6) args[5].toFloat() else 0f
        return Location(world, x, y, z, yaw, pitch)
    }
}