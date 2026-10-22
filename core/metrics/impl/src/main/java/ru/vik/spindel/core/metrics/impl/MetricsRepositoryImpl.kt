package ru.vik.spindel.core.metrics.impl

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import ru.vik.spindel.core.database.api.DatabaseRepository
import ru.vik.spindel.core.metrics.api.Metric
import ru.vik.spindel.core.metrics.api.MetricsRepository
import ru.vik.spindel.core.network.api.NetworkRepository

class MetricsRepositoryImpl @Inject constructor(
    private val database: DatabaseRepository,
    private val network: NetworkRepository
) : MetricsRepository {
    override fun observeMetrics(brewId: Long): Flow<List<Metric>> = database.observeMetrics(brewId)

    override suspend fun refreshMetrics(brewId: Long): Result<Unit> {
        return network.getMetricList(brewId).mapCatching { list ->
            database.addMetrics(brewId, list)
        }
    }
}