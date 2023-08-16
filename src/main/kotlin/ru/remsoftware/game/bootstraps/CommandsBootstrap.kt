package ru.remsoftware.game.bootstraps

import reactor.core.publisher.Mono
import ru.remsoftware.game.commands.KitpvpCommands
import ru.remsoftware.game.commands.KitpvpTabComplete
import ru.starfarm.core.CorePlugin
import ru.tinkoff.kora.application.graph.Lifecycle
import ru.tinkoff.kora.common.Component
import ru.tinkoff.kora.common.annotation.Root

@Root
@Component
class CommandsBootstrap(
    private val commands: KitpvpCommands,
    private val plugin: CorePlugin,
    private val kitpvpTabComplete: KitpvpTabComplete,
) : Lifecycle {
    override fun init(): Mono<*> {

        plugin.server.getPluginCommand("kitpvp").executor = commands
        plugin.server.getPluginCommand("kitpvp").tabCompleter = kitpvpTabComplete

        return Mono.empty<Nothing>()
    }

    override fun release(): Mono<*> = Mono.empty<Nothing>()

}

