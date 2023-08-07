package ru.remsoftware.game.player

import ru.remsoftware.database.DataBaseRepository

class PlayerLoader(
    private val pName: String,
    private val dataBase: DataBaseRepository,
) {
    var name = pName
        private set
    var kit = "default"
        private set
    var money = 0
        private set
    var donateGroup = "default"
        private set
    var arena = "lobby"
        private set
    var kills = 0
        private set
    var currentKills = 0
        private set
    var deaths = 0
        private set
    var localBooster: Double = 1.0
        private set
    var activeBooster: Boolean = false
        private set
    var boosterTime: Long = 0L
        private set

    init {
        var playerData = dataBase.loadPlayerData(name)
        if (playerData == null) {
            playerData = KitPlayer(name, kit, money, donateGroup, arena, kills, currentKills, deaths, localBooster, activeBooster, boosterTime)
            dataBase.createPlayer(playerData)
        }
        this.name = playerData.name
        this.kit = playerData.kit
        this.money = playerData.money
        this.donateGroup = playerData.donateGroup
        this.arena = playerData.arena
        this.kills = playerData.kills
        this.currentKills = playerData.currentKills
        this.deaths = playerData.deaths
        this.localBooster = playerData.localBooster
        this.activeBooster = playerData.activeBooster
        this.boosterTime = playerData.boosterTime
    }


}
