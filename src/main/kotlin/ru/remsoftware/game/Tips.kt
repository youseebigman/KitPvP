package ru.remsoftware.game

enum class Tips(val tip: String) {
    SIGN_TIPS(
        "&8[&b&lKit&4&lPvP&8]&c&l Вы неправильно ввели команду! \n" +
                "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp sign work &f- Включить/Выключить режим работы с табличками \n" +
                "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp sign create [reward] [cooldown] &f- Создать табличку с монетами \n" +
                "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp sign update [reward] [cooldown] &f- Обновить табличку с монетами"
    ),
    KITPVP_HELP_TIPS(
        "&8[&b&lKit&4&lPvP&8]&2&l Справка помощи для команды /kitpvp \n" +
                "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp booster add [playerName] [seconds] &f- Выдать бустер игроку\n" +
                "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp sign work &f- Включить/Выключить режим работы с табличками\n" +
                "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp sign create [reward] [cooldown] &f- Создать табличку с монетами\n" +
                "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp sign update [reward] [cooldown] &f- Обновить табличку с монетами"
    ),
    BOOSTER_TIPS(
        "&8[&b&lKit&4&lPvP&8]&c&l Вы не ввели нужные данные!\n" +
                "&8[&b&lKit&4&lPvP&8]&e&l /kitpvp booster add [playerName] [seconds]"
    ),
}