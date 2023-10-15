package ru.remsoftware.game.player

import ru.remsoftware.database.DataBaseRepository

class PlayerLoader(
    private val pName: String,
    private val dataBase: DataBaseRepository,
) {
    var name = pName
        private set
    var gameData: String? = null
        private set
    var potionEffects: String? = null
        private set
    var kit = "default"
        private set
    var money = 0
        private set
    var donateGroup = "default"
        private set
    var arena = "spawn"
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
    var boosterTime: Int = 0
        private set
    var position: String? = null
        private set
    var inventory: String? = null
        private set
    var availableKits: String? = null
        private set

    init {
        var playerData = dataBase.loadPlayerData(name)
        if (playerData == null) {
            playerData = KitPlayer(name, gameData, potionEffects, kit, money, donateGroup, arena, kills, currentKills, deaths, localBooster, activeBooster, boosterTime, position, inventory, availableKits)
            dataBase.createPlayer(playerData)
        }
        this.name = playerData.name
        this.gameData = playerData.gameData
        this.potionEffects = playerData.potionEffects
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
        this.position = playerData.position
        this.inventory = playerData.inventory
        this.availableKits = playerData.availableKits
    }


}
