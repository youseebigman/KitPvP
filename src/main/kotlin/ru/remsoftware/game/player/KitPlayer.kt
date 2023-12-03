package ru.remsoftware.game.player

data class KitPlayer(
    var name: String,
    var gameData: String?,
    var potionEffects: String?,
    var kit: String,
    var money: Int,
    var donateGroup: Int,
    var arena: String,
    var kills: Int,
    var currentKills: Int,
    var deaths: Int,
    var localBooster: Double,
    var activeBooster: Boolean,
    var boosterTime: Int,
    var position: String?,
    var inventory: String?,
    var availableKits: String?,
    var permissions: String,
)
