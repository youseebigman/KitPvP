package ru.remsoftware.game.listeners

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import ru.remsoftware.game.menus.MainMenu
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.signs.SignService
import ru.remsoftware.utils.VariationMessages
import ru.starfarm.core.util.format.ChatUtil
import ru.starfarm.core.util.item.material
import ru.tinkoff.kora.common.Component

@Component
class PlayerInteractListener (
    private val signService: SignService,
    private val moneyManager: MoneyManager,
    private val mainMenu: MainMenu,
) : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val action = event.action
        if (event.item != null) {
            val handItem = event.item
            if (handItem.material == Material.COMPASS) {
                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                    mainMenu.openInventory(player)
                }
            }
        }
        if (event.clickedBlock != null) {
            val block = event.clickedBlock
            if (block.type.equals(Material.SIGN_POST) || block.type.equals(Material.WALL_SIGN)) {
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
                            VariationMessages.sendMessageWithVariants(moneySign.remainingTime.toInt(), player, "cooldown")
                        }
                    }
                }
            }
        }
    }
}