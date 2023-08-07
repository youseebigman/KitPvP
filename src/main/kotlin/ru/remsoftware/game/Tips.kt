package ru.remsoftware.game

enum class Tips(val tip: String) {
    SIGNTIPS("&8[&b&lKit&4&lPvP&8]&c&l Вы неправильно ввели команду! \n" +
            "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp sign work &f- Включить/Выключить режим работы с табличками \n" +
            "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp sign create [reward] [cooldown(sec)] &f- Создать табличку с монетами \n" +
            "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp sign update [reward] [cooldown(sec)] &f- Обновить табличку с монетами")
}