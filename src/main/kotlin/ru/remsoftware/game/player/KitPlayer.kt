package ru.remsoftware.game.player

import org.bukkit.Location

data class KitPlayer(
    var name: String,
    var gameData: String?,
    var potionEffects: String?,
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
    var position: String?,
    var inventory: String?,
)
