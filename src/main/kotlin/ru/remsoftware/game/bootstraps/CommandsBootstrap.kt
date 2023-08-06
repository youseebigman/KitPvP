package ru.remsoftware.game.bootstraps

import org.bukkit.entity.Player
import reactor.core.publisher.Mono
import ru.remsoftware.game.commands.KitpvpCommands
import ru.starfarm.core.CorePlugin
import ru.starfarm.core.command.Command
import ru.tinkoff.kora.application.graph.All
import ru.tinkoff.kora.application.graph.Lifecycle
import ru.tinkoff.kora.common.Component
import ru.tinkoff.kora.common.annotation.Root

@Root
@Component
class CommandsBootstrap(
    private val plugin: CorePlugin,
    private val kitpvpCommands: KitpvpCommands,
) : Lifecycle {
    override fun init(): Mono<*> {
        plugin.server.getPluginCommand("kitpvp").executor = kitpvpCommands
        plugin.server.getPluginCommand("kitpvp").tabCompleter = kitpvpCommands

        return Mono.empty<Nothing>()
    }

    override fun release(): Mono<*> = Mono.empty<Nothing>()

}

