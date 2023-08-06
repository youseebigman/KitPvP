package ru.remsoftware.game.signs

data class MoneySignData(
    val location: String,
    var reward: Int,
    var status: Boolean,
    var cooldown: Long,
    var remainingTime: Long,

)