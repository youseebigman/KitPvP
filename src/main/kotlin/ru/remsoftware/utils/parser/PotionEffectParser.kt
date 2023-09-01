package ru.remsoftware.utils.parser

import com.google.gson.*
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.tinkoff.kora.common.Component

@Component
class PotionEffectParser {

    fun effectsToJson(player: Player): String {
        val effectsArray = JsonArray()
        val potionEffects = player.activePotionEffects
        for (effect in potionEffects) {
            val effectObject = JsonObject()
            val effectJson = potionEffectToJson(effect)
            val effectJs = JsonParser().parse(effectJson)
            effectsArray.add(effectJs)
        }
        return Gson().toJson(effectsArray)
    }

    fun jsonToPotionEffect(json: String): MutableList<PotionEffect> {
        val effectsList = mutableListOf<PotionEffect>()
        val effectJsonMap = JsonParser().parse(json)
        val effectObj = effectJsonMap.asJsonArray
        for (effect in effectObj) {
            val potionEffectObject = effect.asJsonObject
            val typeId = potionEffectObject.get("effect").asInt
            val type = PotionEffectType.getById(typeId)
            val duration = potionEffectObject.get("duration").asInt
            val amplifier = potionEffectObject.get("amplifier").asInt
            val ambient = potionEffectObject.get("ambient").asBoolean
            val particles = potionEffectObject.get("has-particles").asBoolean
            effectsList.add(PotionEffect(type, duration, amplifier, ambient, particles))
        }
        return effectsList
    }

    fun potionEffectToJson(potionEffect: PotionEffect): String {
        val mapEffect = potionEffect.serialize()
        return Gson().toJson(mapEffect)
    }

}