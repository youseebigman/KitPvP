package ru.remsoftware.game.commands

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import reactor.core.publisher.Mono
import ru.starfarm.core.CorePlugin
import ru.tinkoff.kora.application.graph.Lifecycle
import ru.tinkoff.kora.common.Component
import ru.tinkoff.kora.common.annotation.Root
import java.lang.reflect.Field

@Root
@Component
class RegisterCommands
    (
    private val plugin: CorePlugin,

) : Lifecycle {

    override fun init(): Mono<*> {
        val bukkitCommandMap: Field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        bukkitCommandMap.isAccessible = true
        val commandMap: CommandMap = bukkitCommandMap.get(Bukkit.getServer()) as CommandMap

        return Mono.empty<Nothing>()
    }

    override fun release(): Mono<*> = Mono.empty<Nothing>()


}
