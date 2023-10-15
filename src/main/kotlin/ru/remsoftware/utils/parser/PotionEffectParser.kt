package ru.remsoftware.utils.parser

import com.google.gson.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import ru.remsoftware.game.player.PlayerDamageService
import ru.tinkoff.kora.common.Component

@Component
class PotionEffectParser(
    private val playerDamageService: PlayerDamageService,
) {

    fun effectsToJson(player: Player): String {
        val effectsArray = JsonArray()
        val potionEffects = player.activePotionEffects
        for (effect in potionEffects) {
            if (effect.type.equals(PotionEffectType.ABSORPTION)) {
                val abs = playerDamageService.hasAbsorption
                if (abs != null && !abs) break
            }
            val effectJson = potionEffectToJson(effect)
            val effectJs = JsonParser().parse(effectJson)
            effectsArray.add(effectJs)
        }
        return Gson().toJson(effectsArray)
    }

    fun effectsInPotionToPlayer(itemStack: ItemStack, player: Player) {
        val itemMeta = itemStack.itemMeta
        val potionEffectList = arrayListOf<PotionEffect>()
        val potionDataList = arrayListOf<PotionData>()
        if (itemMeta is PotionMeta) {
            if (itemMeta.hasCustomEffects()) {
                itemMeta.customEffects.forEach {
                    val type = it.type
                    val amplifier = it.amplifier
                    val duration = it.duration
                    val ambient = it.isAmbient
                    val particles = it.hasParticles()
                    val color = it.color
                    potionEffectList.add(PotionEffect(type, duration, amplifier, ambient, particles, color))
                }
            } else {
                val type = itemMeta.basePotionData.type
                val isExtended = itemMeta.basePotionData.isExtended
                val isUpgraded = itemMeta.basePotionData.isUpgraded
                potionDataList.add(PotionData(type, isExtended, isUpgraded))
            }
            potionEffectList.forEach {
                player.addPotionEffect(it)
            }
            potionDataList.forEach {
                val pe = potionDataToPotionEffect(it)
                player.addPotionEffect(pe)
            }
        }
    }
    fun potionDataToPotionEffect(data: PotionData): PotionEffect {
        val effectType = data.type.effectType
        val level = if (data.isUpgraded) 1 else 0
        var duration: Int? = null
        if (!data.isUpgraded && data.isExtended) duration = 480000
        else if ((!data.isUpgraded && !data.isExtended) || (data.isUpgraded && data.isExtended)) duration = 180000
        else if (data.isUpgraded && !data.isExtended) duration = 90000
        return PotionEffect(effectType, level, duration!!)
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
            val newAmplifier = playerDamageService.newAmplifier
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