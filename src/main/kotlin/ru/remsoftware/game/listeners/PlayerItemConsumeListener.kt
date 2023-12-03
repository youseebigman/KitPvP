package ru.remsoftware.game.listeners

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.remsoftware.game.player.PlayerAbsorptionService
import ru.remsoftware.game.potions.PotionService
import ru.remsoftware.utils.VariationMessages
import ru.remsoftware.utils.parser.PotionEffectParser
import ru.starfarm.core.util.item.name
import ru.starfarm.core.util.time.CooldownUtil
import ru.tinkoff.kora.common.Component
import java.util.concurrent.TimeUnit

@Component
class PlayerItemConsumeListener(
    private val damageService: PlayerAbsorptionService,
    private val potionService: PotionService,
    private val potionEffectParser: PotionEffectParser,
) : Listener {

    @EventHandler
    fun onPlayerConsume(event: PlayerItemConsumeEvent) {
        val item = event.item
        val player = event.player
        if (item.name != null) {
            val itemName = item.name!!.replace("§", "&")
            val customPotionsNameList = potionService.getAllPotionsName()
            if (customPotionsNameList.contains(itemName)) {
                val customPotion = potionService[itemName]
                if (customPotion != null) {
                    if (CooldownUtil.has(itemName, player)) {
                        event.isCancelled = true
                        val timeLeft = TimeUnit.MILLISECONDS.toSeconds(CooldownUtil.get(itemName, player))
                        VariationMessages.sendMessageWithVariants(timeLeft.toInt(), player, "potion_wait", null, null)
                        player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
                    } else {
                        event.isCancelled = true
                        effectsInPotionToPlayer(item, player)
                        CooldownUtil.put(itemName, player, customPotion.cooldown)
                    }
                } else {
                    println("Potion not created!")
                }
            }
        }

        if (item.type == Material.GOLDEN_APPLE) {
            event.isCancelled = true
            damageService.hasAbsorption = true
            damageService.invalidate(event.player.name)
        }
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
                if (player.hasPotionEffect(it.type)) {
                    if (it.type.equals(PotionEffectType.HEALTH_BOOST)) {
                        val hp = player.health
                        player.health = 20.0
                        player.removePotionEffect(it.type)
                        player.addPotionEffect(it)
                        player.health = hp
                    } else {
                        player.removePotionEffect(it.type)
                        player.addPotionEffect(it)
                    }
                }
                player.addPotionEffect(it)
            }
            potionDataList.forEach {
                val pe = potionDataToPotionEffect(it)
                player.addPotionEffect(pe)
            }
        }
    }
    fun potionDataToPotionEffect(data: PotionData): PotionEffect {
        val effectType = PotionEffectType.getByName(data.type.effectType.name)
        val level = if (data.isUpgraded) 1 else 0
        var duration: Int? = null
        if (!data.isUpgraded && data.isExtended) duration = 480000
        else if ((!data.isUpgraded && !data.isExtended) || (data.isUpgraded && data.isExtended)) duration = 180000
        else if (data.isUpgraded && !data.isExtended) duration = 90000
        return PotionEffect(effectType, level, duration!!)
    }


}