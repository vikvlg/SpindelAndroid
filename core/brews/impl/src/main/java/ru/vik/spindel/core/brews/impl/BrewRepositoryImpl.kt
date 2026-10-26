package ru.vik.spindel.core.brews.impl

import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException
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

    override suspend fun getBrew(brewId: Long): Result<Brew> {
        try {
            val brew = database.getBrew(brewId) ?: return Result.failure(Exception("Not found"))
            return Result.success(brew)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }
}