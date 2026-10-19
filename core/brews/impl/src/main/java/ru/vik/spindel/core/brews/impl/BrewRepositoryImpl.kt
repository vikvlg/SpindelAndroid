package ru.vik.spindel.core.brews.impl

import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.brews.api.BrewRepository
import ru.vik.spindel.core.network.api.NetworkRepository
import kotlin.time.Duration.Companion.milliseconds

class BrewRepositoryImpl @Inject constructor(
    private val network: NetworkRepository
) : BrewRepository {
    override fun observeBrews(): Flow<List<Brew>> = flow {
        // TODO: Доставать данные из БД.
        delay(500.milliseconds)
        network.getBrewList().onSuccess {
            emit(it)
        }.onFailure {  }
    }

    override suspend fun refreshBrews(): Result<Unit> {
        // TODO: Запрос в сеть.
        delay(1500.milliseconds)
        return Result.success(Unit)
    }
}