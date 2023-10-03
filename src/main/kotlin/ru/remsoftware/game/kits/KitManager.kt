package ru.remsoftware.game.kits

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.parser.InventoryParser
import ru.remsoftware.utils.parser.PotionEffectParser
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class KitManager(
    private val kitService: KitService,
    private val playerService: PlayerService,
    private val inventoryParser: InventoryParser,
    private val potionEffectParser: PotionEffectParser,
) {
    fun buyKit(player: Player, data: KitData) {
        val kitPlayer = playerService.get(player)!!
        val playerBalance = kitPlayer.money
        val price = data.price
        if (playerBalance < price) {
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c У вас недостаточно монет для покупки этого класса")
            player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F)
        } else {
            val newBalance = playerBalance - price
            kitPlayer.money = newBalance
            playerService[player.name] = kitPlayer
            setKit(player, data)
        }

    }

    fun setKit(player: Player, data: KitData) {
        val kitPlayer = playerService.get(player)
        if (kitPlayer != null) {
            kitPlayer.kit = data.name
            kitPlayer.inventory = data.inventory
            val kitInventory = inventoryParser.jsonToInventory(data.inventory)
            val kitPotionEffect = potionEffectParser.jsonToPotionEffect(data.potionEffects)
            player.inventory.clear()
            var counter = 0
            for (item in kitInventory) {
                if (item == null) {
                    player.inventory.setItem(counter, ItemStack(Material.AIR))
                    counter++
                } else {
                    player.inventory.setItem(counter, item)
                    counter++
                }
            }
            for (potionEffect in kitPotionEffect) {
                player.addPotionEffect(potionEffect)
            }
        }
    }
    fun createKit(database: DataBaseRepository, player: Player, name: String, price: Int) {
        if (kitService[name] != null) {
            ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Кит с таким названием уже существует")
        } else {
            val icon = player.inventory.itemInOffHand.type.toString()
            player.inventory.clear(40)
            val kitInventory = inventoryParser.inventoryToJson(player.inventory)
            val kitPotionEffect = potionEffectParser.effectsToJson(player)
            val kitData = KitData(name, icon, kitInventory, kitPotionEffect, price)
            kitService.createKit(kitData, database)
        }

    }
}