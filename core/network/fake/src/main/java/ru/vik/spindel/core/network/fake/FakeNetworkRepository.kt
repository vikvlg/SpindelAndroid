package ru.vik.spindel.core.network.fake

import jakarta.inject.Inject
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.network.api.NetworkRepository
import java.time.LocalDate

class FakeNetworkRepository @Inject constructor() : NetworkRepository {
    private var key: Long = 0
    private val list: List<Brew> = listOf(
        Brew(++key, "Стаут", LocalDate.of(2026, 2, 8)),
        Brew(++key, "Первомайская варка", LocalDate.of(2026, 5, 1)),
        Brew(++key, "Майская варка", LocalDate.of(2026, 5, 20)),
        Brew(++key, "Светлый эль", LocalDate.of(2026, 7, 1)),
        Brew(++key, "IPA осенний", LocalDate.of(2026, 9, 18)),
    )

    override fun getBrew(brewId: Long): Result<Brew> {
        val brew = list.find { it.id == brewId }
        return if (brew != null)
            Result.success(brew)
        else
            Result.failure(Exception("Не найдено"))
    }

    override fun getBrewList(): Result<List<Brew>> {
        return Result.success(list)
    }
}