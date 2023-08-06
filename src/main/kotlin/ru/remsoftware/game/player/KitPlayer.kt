package ru.remsoftware.game.player

import java.util.Arrays
import java.util.UUID


data class KitPlayer(
    var name: String,
    var kit: String,
    var money: Int,
    var donateGroup: String,
    var arena: String,
    var kills: Int,
    var currentKills: Int,
    var deaths: Int,
    var localBooster: Double,
    var activeBooster: Boolean,
    var boosterTime: Long,
)
