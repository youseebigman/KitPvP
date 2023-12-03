package ru.remsoftware

import org.bukkit.Material
import ru.dargen.board.BoardAPI
import ru.remsoftware.kora.ApplicationGraph
import ru.starfarm.core.ApiManager
import ru.starfarm.core.CorePlugin
import ru.starfarm.core.chat.IChatService
import ru.starfarm.core.profile.group.DonateGroup
import ru.starfarm.core.scoreboard.IScoreboardService
import ru.starfarm.core.scoreboard.ScoreboardService
import ru.tinkoff.kora.application.graph.KoraApplication


class Kitpvp : CorePlugin() {

    override fun enable() {
        KoraApplication.run(ApplicationGraph::graph)
        BoardAPI.init(this)

        unregisterService(IChatService::class.java)

        registerService(IScoreboardService::class.java, ScoreboardService())
    }

    override fun disable() {
    }

    override fun handleTowerConnect() {
        ApiManager.newDonateAbilitiesBuilder("§b§lKit§4§lPvP", Material.IRON_SWORD)
            .ability(
                DonateGroup.VIP,
                "&b• &fБустер монет - &a5%",
                "&b• &fВремя телепортации снижено до &a7 секунд"
            )
            .ability(
                DonateGroup.VIP_PLUS,
                "&b• &fБустер монет - &a5%",
                "&b• &fВремя телепортации снижено до &a6 секунд"
            )
            .ability(
                DonateGroup.PREMIUM,
                "&b• &fБустер монет - &a10%",
                "&b• &fВремя телепортации снижено до &a6 секунд"
            )
            .ability(
                DonateGroup.PREMIUM_PLUS,
                "&b• &fБустер монет - &a15%",
                "&b• &fВремя телепортации снижено до &a6 секунд"
            )
            .ability(
                DonateGroup.ELITE,
                "&b• &fБустер монет - &a20%",
                "&b• &fВремя телепортации снижено до &a5 секунд",
                "&b• &fБесплатный выбор &e&lELITE &fкитов"
            )
            .ability(
                DonateGroup.ELITE_PLUS,
                "&b• &fБустер монет - &a27%",
                "&b• &fВремя телепортации снижено до &a5 секунд",
                "&b• &fБесплатный выбор &e&lELITE &fкитов"
            )
            .ability(
                DonateGroup.SPONSOR,
                "&b• &fБустер монет - &a35%",
                "&b• &fВремя телепортации снижено до &a4 секунд",
                "&b• &fБесплатный выбор &6&lSPONSOR &fкитов"
            )
            .ability(
                DonateGroup.SPONSOR_PLUS,
                "&b• &fБустер монет - &a40%",
                "&b• &fВремя телепортации снижено до &a4 секунд",
                "&b• &fБесплатный выбор &6&lSPONSOR &fкитов"
            )
            .ability(
                DonateGroup.UNIQUE,
                "&b• &fБустер монет - &a50%",
                "&b• &fВремя телепортации снижено до &a3 секунд",
                "&b• &fБесплатный выбор &3&lUNIQUE &fкитов"
            )
            .register()

    }
}