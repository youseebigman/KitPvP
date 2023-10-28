package ru.remsoftware.game.menus

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import ru.remsoftware.game.arena.ArenaService
import ru.remsoftware.game.kits.KitManager
import ru.remsoftware.game.kits.KitService
import ru.remsoftware.game.money.MoneyManager
import ru.remsoftware.game.player.PlayerService
import ru.remsoftware.utils.parser.InventoryParser
import ru.starfarm.core.ApiManager
import ru.starfarm.core.inventory.container.InventoryContainer
import ru.starfarm.core.util.format.ChatUtil

class ArenasMenu(
    private val kitManager: KitManager,
    private val kitService: KitService,
    private val menuUtil: MenuUtil,
    private val moneyManager: MoneyManager,
    private val playerService: PlayerService,
    private val arenaService: ArenaService,
    private val inventoryParser: InventoryParser,
) : InventoryContainer("Выбор Арены", 6) {
    override fun drawInventory(player: Player) {
        val nukeItem = ApiManager.newItemBuilder(Material.STONE, data = 6).apply {
            name = "&fNuke"
            lore(
                "",
                "&7Нажмите, чтобы телепортироваться на арену"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        val gladiatorArenaItem = ApiManager.newItemBuilder(Material.SANDSTONE, data = 2).apply {
            name = "&fГладиаторская арена"
            lore(
                "",
                "&7Нажмите, чтобы телепортироваться на арену"
            )
            addItemFlags(*ItemFlag.values())
        }.build()
        addItem(12, gladiatorArenaItem) { _, _ ->
            val kitPlayer = playerService[player]
            if (kitPlayer!!.kit.equals("default")) {
                player.closeInventory()
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Чтобы телепортироваться на арену, сначала выберете кит!")
                player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
            } else {
                arenaService.teleportOnRandomSpawnPoints("arena", player)
            }
        }

        addItem(13, nukeItem) { _, _ ->
            val kitPlayer = playerService[player]
            if (kitPlayer!!.kit.equals("default")) {
                player.closeInventory()
                ChatUtil.sendMessage(player, "&8[&b&lKit&4&lPvP&8]&c Чтобы телепортироваться на арену, сначала выберете кит!")
                player.playSound(player.eyeLocation, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f)
            } else {
                arenaService.teleportOnRandomSpawnPoints("nuke", player)
            }
        }
        addItem(45, menuUtil.backButton) { _, _ ->
            MainMenu(kitManager, kitService, menuUtil, moneyManager, playerService, arenaService, inventoryParser).openInventory(player)
        }
    }
}