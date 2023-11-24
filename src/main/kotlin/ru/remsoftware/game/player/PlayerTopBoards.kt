package ru.remsoftware.game.player

import com.typesafe.config.Config
import net.minecraft.server.v1_12_R1.EnumDirection
import org.bukkit.entity.Player
import ru.dargen.board.BannerBoard
import ru.dargen.board.BoardAPI
import ru.remsoftware.database.DataBaseRepository
import ru.remsoftware.utils.Logger
import ru.remsoftware.utils.VariationMessages
import ru.remsoftware.utils.config.PluginConfigLoader
import ru.remsoftware.utils.parser.LocationParser
import ru.starfarm.core.CorePlugin
import ru.starfarm.core.task.GlobalTaskContext
import ru.tinkoff.kora.common.Component
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO

@Component
class PlayerTopBoards(
    private val dataBase: DataBaseRepository,
    private val logger: Logger,
    private val pluginConfigLoader: PluginConfigLoader,
    private val plugin: CorePlugin,
    private val locationParser: LocationParser,
) {
    private var playerMoneyTop = listOf<Pair<String, Int>>()
    private var playerKillTop = listOf<Pair<String, Int>>()

    private val boardCache = hashMapOf<String, BannerBoard>()

    init {
        val backgroundFolder = File("${plugin.dataFolder.absolutePath}/backgrounds")
        val boardConfig: Config = pluginConfigLoader.load("boards.conf")
        val boardList = boardConfig.getConfigList("boards")
        var hasBoard = false

        GlobalTaskContext.everyAsync(10, 600 * 20) {
            val playerDataList = dataBase.loadAllPlayerData()
            val moneyTop = sortPlayerData(playerDataList, "money")
            val killTop = sortPlayerData(playerDataList, "kills")
            playerMoneyTop = if (moneyTop.size < 10) moneyTop.take(moneyTop.size) else moneyTop.take(10)
            playerKillTop = if (killTop.size < 10) killTop.take(killTop.size) else killTop.take(10)
            if (!hasBoard) {
                createBoards(boardList, backgroundFolder)
                hasBoard = true
            }
            else {
                boardCache.values.forEach {
                    BoardAPI.getApi().unregister(it)
                }
                boardCache.clear()
                createBoards(boardList, backgroundFolder)
            }

        }
    }
    fun createBoards(boardList: List<Config>, backgroundFolder: File) {
        boardList.forEach { config ->
            val name = config.getString("name")
            val location = locationParser.strToLoc(config.getString("location"))
            val direction = EnumDirection.valueOf(config.getString("direction"))
            val imageName = config.getString("image")
            val background = backgroundFolder.listFiles()?.find { imageName == it.name }.let {
                ImageIO.read(it).getScaledInstance(512, 512, Image.SCALE_DEFAULT)
            }
            val board = BoardAPI.createBanner(location, direction, 4, 4)
            val renderer = board.renderer
            renderer.createGraphics()

            renderer.graphics.also { graphics ->
                var pos = 1
                graphics.drawImage(background, 0, 0, null)
                if (name.equals("money")) {
                    graphics.font = graphics.font.deriveFont(1, 35.0F)
                    renderer.drawCenteredString("§aТОП ПО БАЛАНСУ", 58)
                    graphics.font = graphics.font.deriveFont(0, 25.0F)
                    playerMoneyTop.forEach {
                        val money = it.second
                        renderer.drawCenteredString("§f${it.first} §7- §a$$money", pos * 42 + 60)
                        pos++
                    }
                } else if (name.equals("kills")) {
                    graphics.font = graphics.font.deriveFont(1, 35.0F)
                    renderer.drawCenteredString("§aТОП ПО УБИЙСТВАМ", 58)
                    graphics.font = graphics.font.deriveFont(0, 25.0F)
                    playerKillTop.forEach {
                        val kills = it.second
                        renderer.drawCenteredString("§f${it.first} §7- §c$kills §f${VariationMessages.returnKillsVariants(kills)}", pos * 42 + 60)
                        pos++
                    }
                }
                board.updateFragments()
            }.dispose()
            boardCache[name] = board
        }
    }

    fun createBoard(player: Player) {
        val loc = player.eyeLocation.add(0.0, 2.0, 0.0)
        val board = BoardAPI.createBanner(loc, EnumDirection.EAST, 5, 4)
        val renderer = board.renderer
        renderer.createGraphics()
        val graphics = renderer.graphics
        graphics.font = graphics.font.deriveFont(0, 25.0F)
        renderer.drawCenteredString("§bHello §a${player.name}", board.renderer.height / 2)
        board.updateFragments()
        graphics.dispose()
    }

    fun sortPlayerData(list: List<KitPlayer>, data: String): List<Pair<String, Int>> {
        val unsortedMap = hashMapOf<String, Int>()
        for (playerData in list) {
            val name = playerData.name
            if (data.equals("money")) {
                val value = playerData.money
                unsortedMap[name] = value
            } else if (data.equals("kills")) {
                val value = playerData.kills
                unsortedMap[name] = value
            }
        }
        return unsortedMap.toList().sortedByDescending { (_, value) -> value }
    }


}