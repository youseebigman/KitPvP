package ru.remsoftware.game.signs

import org.bukkit.Location

data class MoneySignEntity(
    val location: Location,
    var reward: Int,
    var status: Boolean,
    var cooldown: Long,
    var remainingTime: Long,
)