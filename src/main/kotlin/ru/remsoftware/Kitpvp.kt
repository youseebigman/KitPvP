package ru.remsoftware

import ru.remsoftware.kora.ApplicationGraph
import ru.starfarm.core.CorePlugin
import ru.starfarm.core.chat.IChatService
import ru.starfarm.core.scoreboard.IScoreboardService
import ru.starfarm.core.scoreboard.ScoreboardService
import ru.tinkoff.kora.application.graph.KoraApplication


class Kitpvp : CorePlugin() {

    override fun enable() {
        //Class.forName("org.mysql.cj.jdbc.Driver")
        KoraApplication.run(ApplicationGraph::graph)
        unregisterService(IChatService::class.java)
        registerService(IScoreboardService::class.java, ScoreboardService())
    }


    override fun disable() {
    }
}