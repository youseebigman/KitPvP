package ru.remsoftware

import ru.dargen.board.BoardAPI
import ru.remsoftware.kora.ApplicationGraph
import ru.starfarm.core.CorePlugin
import ru.starfarm.core.chat.IChatService
import ru.starfarm.core.hologram.IHologramService
import ru.starfarm.core.scoreboard.IScoreboardService
import ru.starfarm.core.scoreboard.ScoreboardService
import ru.starfarm.core.tab.ITabService
import ru.tinkoff.kora.application.graph.KoraApplication


class Kitpvp : CorePlugin() {

    override fun enable() {
        KoraApplication.run(ApplicationGraph::graph)
        BoardAPI.init(this)

        unregisterService(IChatService::class.java)
        unregisterService(IHologramService::class.java)

        registerService(IScoreboardService::class.java, ScoreboardService())
    }


    override fun disable() {
    }
}