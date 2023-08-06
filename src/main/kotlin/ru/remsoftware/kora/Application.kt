package ru.remsoftware.kora

import ru.tinkoff.kora.common.KoraApp
import ru.tinkoff.kora.config.common.DefaultConfigExtractorsModule
import ru.tinkoff.kora.database.jdbc.JdbcDatabaseModule

@KoraApp
interface Application : JdbcDatabaseModule, DefaultConfigExtractorsModule {
}