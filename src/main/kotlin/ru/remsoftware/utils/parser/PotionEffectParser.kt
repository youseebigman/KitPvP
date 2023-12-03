package ru.remsoftware.utils.parser

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.remsoftware.game.player.PlayerAbsorptionService
import ru.tinkoff.kora.common.Component

@Component
class PotionEffectParser(
    private val playerAbsorptionService: PlayerAbsorptionService,
) {

    fun effectsToJson(player: Player): String {
        val effectsArray = JsonArray()
        val potionEffects = player.activePotionEffects
        for (effect in potionEffects) {
            if (effect.type.equals(PotionEffectType.ABSORPTION)) {
                val abs = playerAbsorptionService.hasAbsorption
                if (abs != null && !abs) break
            }
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
            if (!effect.equals("null")) {
                val potionEffectObject = effect.asJsonObject
                val typeId = potionEffectObject.get("effect").asInt
                val type = PotionEffectType.getById(typeId)
                val duration = potionEffectObject.get("duration").asInt
                val amplifier = potionEffectObject.get("amplifier").asInt
                val ambient = potionEffectObject.get("ambient").asBoolean
                val particles = potionEffectObject.get("has-particles").asBoolean
                effectsList.add(PotionEffect(type, duration, amplifier, ambient, particles))
            }
        }
        return effectsList
    }

    fun potionEffectToJson(potionEffect: PotionEffect): String {
        if (potionEffect.type.equals(PotionEffectType.ABSORPTION)) {
            val newAmplifier = playerAbsorptionService.newAmplifier
            if (newAmplifier != null) {
                val pe = PotionEffect(PotionEffectType.ABSORPTION, potionEffect.duration, newAmplifier, potionEffect.isAmbient, potionEffect.hasParticles())
                val peMap = pe.serialize()
                return Gson().toJson(peMap)
            } else {
                val pe = PotionEffect(PotionEffectType.ABSORPTION, potionEffect.duration, potionEffect.amplifier, potionEffect.isAmbient, potionEffect.hasParticles())
                val peMap = pe.serialize()
                return Gson().toJson(peMap)
            }
        } else {
            val peMap = potionEffect.serialize()
            return Gson().toJson(peMap)
        }
    }

}