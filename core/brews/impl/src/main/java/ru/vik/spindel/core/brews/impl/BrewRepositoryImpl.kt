package ru.vik.spindel.core.brews.impl

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.brews.api.BrewRepository
import ru.vik.spindel.core.database.api.DatabaseRepository
import ru.vik.spindel.core.network.api.NetworkRepository

class BrewRepositoryImpl @Inject constructor(
    private val database: DatabaseRepository,
    private val network: NetworkRepository,
) : BrewRepository {
    override fun observeBrews(): Flow<List<Brew>> = database.observeBrews()

    override suspend fun refreshBrews(): Result<Unit> {
        return network.getBrewList().mapCatching { list ->
            database.addBrews(list)
        }
    }
}