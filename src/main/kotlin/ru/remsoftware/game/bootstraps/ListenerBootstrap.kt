package ru.remsoftware.game.bootstraps

import org.bukkit.event.Listener
import reactor.core.publisher.Mono
import ru.starfarm.core.CorePlugin
import ru.tinkoff.kora.application.graph.All
import ru.tinkoff.kora.application.graph.Lifecycle
import ru.tinkoff.kora.common.Component
import ru.tinkoff.kora.common.annotation.Root


@Root
@Component
class ListenerBootstrap(
    private val plugin: CorePlugin,
    private val listeners: All<Listener>,
) : Lifecycle {
    override fun init(): Mono<*> {
        listeners.forEach {
            plugin.eventContext.onListeners(it)
        }

        return Mono.empty<Nothing>()
    }

    override fun release(): Mono<*> = Mono.empty<Nothing>()

}