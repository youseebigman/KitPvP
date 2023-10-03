package ru.remsoftware.game.kits

import ru.remsoftware.utils.parser.PotionEffectParser

data class KitData(
    var name: String,
    var icon: String,
    var inventory: String,
    var potionEffects: String,
    var price: Int,
)
