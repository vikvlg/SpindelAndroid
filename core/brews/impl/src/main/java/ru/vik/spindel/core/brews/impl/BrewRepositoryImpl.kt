package ru.vik.spindel.core.brews.impl

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.brews.api.BrewRepository
import ru.vik.spindel.core.network.api.NetworkRepository

class BrewRepositoryImpl @Inject constructor(
    private val network: NetworkRepository
) : BrewRepository {
    override fun observeBrews(): Flow<List<Brew>> = flow {
        network.getBrewList().onSuccess {
            emit(it)
        }.onFailure {  }
    }

    override suspend fun refreshBrews(): Result<Unit> {
        return Result.success(Unit)
    }
}