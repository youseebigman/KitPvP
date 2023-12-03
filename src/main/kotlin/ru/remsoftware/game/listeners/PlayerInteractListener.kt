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
import ru.starfarm.core.ApiManager
import ru.starfarm.core.util.format.ChatUtil
import ru.tinkoff.kora.common.Component

@Component
class PlayerInteractListener(
    private val signService: SignService,
    private val moneyManager: MoneyManager,
    private val mainMenu: MainMenu,
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
            }
        }
        if (event.clickedBlock != null) {

            val block = event.clickedBlock
            val type = block.type
            if (type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
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
            } else if (type.name.equals("ANVIL") || type.equals(Material.WORKBENCH) || type.name.endsWith("BOX") || type.name.endsWith("CHEST") || type.equals(Material.BREWING_STAND) || type.equals(Material.DISPENSER) || type.equals(Material.DROPPER) || type.equals(Material.HOPPER) || type.equals(Material.FURNACE)) {
                if (player.isSneaking) {
                    if (event.item != null) return
                    else event.isCancelled = !player.isOp
                }
                else event.isCancelled = !player.isOp
            }
        }
    }
}