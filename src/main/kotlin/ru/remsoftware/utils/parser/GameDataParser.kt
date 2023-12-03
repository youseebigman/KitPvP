package ru.remsoftware.utils.parser

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import ru.tinkoff.kora.common.Component

@Component
class GameDataParser {
    fun jsonToGameData(json: String, player: Player) {
        val jsonGameData = JsonParser().parse(json)
        val gameDataObject = jsonGameData.asJsonObject
        val maxHealth = gameDataObject.get("max_health").asDouble
        val health = gameDataObject.get("health").asDouble
        val foodLevel = gameDataObject.get("food_level").asInt
        val gameMode = gameDataObject.get("game_mode").asInt

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = maxHealth
        player.health = health
        player.foodLevel = foodLevel
        player.gameMode = GameMode.getByValue(gameMode)

    }


    fun gameDataToJson(player: Player): String {
        val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue
        val health = player.health
        val foodLevel = player.foodLevel
        val gameMode = player.gameMode.value
        val gameDataObject = JsonObject()

        gameDataObject.addProperty("max_health", maxHealth)
        gameDataObject.addProperty("health", health)
        gameDataObject.addProperty("food_level", foodLevel)
        gameDataObject.addProperty("game_mode", gameMode)
        return Gson().toJson(gameDataObject)

    }
}