package ru.remsoftware.game.kits

data class KitData(
    var name: String,
    var icon: String,
    var inventory: String,
    var potionEffects: String,
    var price: Int,
    var donateCooldown: Long?,
    var donateGroup: String?,
)
