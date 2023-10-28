package ru.remsoftware.game.menus

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffectType
import ru.remsoftware.game.potions.PotionService
import ru.remsoftware.utils.parser.InventoryParser
import ru.remsoftware.utils.parser.PotionEffectParser
import ru.starfarm.core.ApiManager
import ru.starfarm.core.inventory.container.InventoryContainer
import ru.tinkoff.kora.common.Component
import java.util.concurrent.TimeUnit

@Component
class PotionMenu(
    private val potionService: PotionService,
    private val inventoryParser: InventoryParser,
    private val potionEffectParser: PotionEffectParser,
): InventoryContainer("Зелья", 6) {
    override fun drawInventory(player: Player) {
        potionService.all().withIndex().forEach {
            val itemStack: ItemStack = inventoryParser.jsonToItem(it.value.potion)
            val itemMeta = itemStack.itemMeta
            val item = ApiManager.newItemBuilder(itemStack.type).apply {
                name = it.value.name
                lore(
                    "",
                    "§2Перезарядка: §b${TimeUnit.MILLISECONDS.toSeconds(it.value.cooldown)} секунд"
                )
                if (itemMeta is PotionMeta) {
                    if (itemMeta.hasCustomEffects()) {
                        itemMeta.customEffects.forEach {
                            addCustomPotionEffect(it, true)
                        }
                    } else {
                        val pd = itemMeta.basePotionData
                        mainPotionEffect(PotionEffectType.getByName(pd.type.name))
                    }
                    if (itemMeta.color != null) {
                        potionColor = itemMeta.color
                    }
                }
            }.build()
            addItem(it.index, item) { _, _ ->
                player.inventory.addItem(item)
            }
        }
    }
}