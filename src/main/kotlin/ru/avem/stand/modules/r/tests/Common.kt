package ru.avem.stand.modules.r.tests

fun calcSyncRPM(F: Int, N: Int): Double {
    for (p in 2..7) {
        val sync = F * 60 / p
        if (N > sync) {
            return (F * 60 / (p - 1)).toDouble()
        }
    }
    return 0.0
}

fun calcZs(syncN: Int): Pair<Double, Double> {
    return when (syncN) {
        500 -> 355.0 to 118.0
        600 -> 355.0 to 140.0
        750 -> 355.0 to 180.0
        1000 -> 355.0 to 236.0
        1500 -> 236.0 to 236.0
        3000 -> 180.0 to 355.0
        else -> throw Exception("Не удалось расчитать параметры шиквов платформы с синхронными оборотами $syncN об/мин")
    }
}
