package ru.remsoftware.game.listeners

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import ru.remsoftware.game.menus.MainMenu
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.potions.PotionService
import ru.remsoftware.game.signs.SignService
import ru.remsoftware.utils.VariationMessages
import ru.starfarm.core.ApiManager
import ru.starfarm.core.task.GlobalTaskContext
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.material
import ru.starfarm.core.util.item.name
import ru.starfarm.core.util.time.CooldownUtil
import ru.tinkoff.kora.common.Component
import java.util.concurrent.TimeUnit

@Component
class PlayerInteractListener(
    private val signService: SignService,
    private val moneyManager: MoneyManager,
    private val mainMenu: MainMenu,
    private val potionService: PotionService,
) : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val action = event.action
        val inventory = player.inventory
        if (event.item != null) {
            val handItem = event.item
            val newItem = ApiManager.newItemBuilder(handItem).apply {}.build()
            if (handItem.type == Material.COMPASS) {
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    mainMenu.openInventory(player)
                }
            } else if (handItem.type == Material.SPLASH_POTION) {
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    if (handItem.name != null) {
                        val itemName = handItem.name!!.replace("§", "&")
                        val customPotionsNameList = potionService.getAllPotionsName()
                        if (customPotionsNameList.contains(itemName)) {
                            val customPotion = potionService[itemName]
                            if (CooldownUtil.has(itemName, player)) {
                                event.isCancelled = true
                                val timeLeft = TimeUnit.MILLISECONDS.toSeconds(CooldownUtil.get(itemName, player))
                                VariationMessages.sendMessageWithVariants(timeLeft.toInt(), player, "potion_wait", null, null)
                                player.playSound(player.eyeLocation, Sound.BLOCK_NOTE_BASS, 1.0f, 1.0f)
                            } else {
                                var slot = 0
                                for (item in inventory.withIndex()) {
                                    if (item.value == handItem) {
                                        slot = item.index
                                        break
                                    }
                                }
                                CooldownUtil.put(itemName, player, customPotion!!.cooldown)
                                if (slot != 0) {
                                    GlobalTaskContext.asyncAfter(5) {
                                        player.inventory.setItem(slot, newItem)
                                        it.cancel()
                                    }
                                } else {
                                    for (itemSlot in inventory.withIndex()) {
                                        if (itemSlot.value == null || itemSlot.value.equals(Material.AIR)) {
                                            player.inventory.setItem(itemSlot.index, newItem)
                                            break
                                        } else {
                                            continue
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        if (event.clickedBlock != null) {
            if (event.clickedBlock.type.equals(Material.SIGN_POST) || event.clickedBlock.type.equals(Material.WALL_SIGN)) {
                val block = event.clickedBlock
                if (signService.signWorkers.contains(player.name)) {
                    signService.selectSign(block)
                    ChatUtil.sendMessage(player, "&aВы успешно выбрали табличку, можете присвоить ей данные")
                } else {
                    val moneySign = signService.get(block.location)
                    if (moneySign == null) {
                        return
                    } else {
                        if (moneySign.status) {
                            moneySign.status = false
                            moneySign.remainingTime = moneySign.cooldown / 1000
                            signService.restore(moneySign)
                            moneyManager.addMoneyWithBoost(moneySign.reward, player)
                        } else {
                            VariationMessages.sendMessageWithVariants(moneySign.remainingTime.toInt(), player, "cooldown", null, null)
                        }
                    }
                }
            }
        }
    }
}