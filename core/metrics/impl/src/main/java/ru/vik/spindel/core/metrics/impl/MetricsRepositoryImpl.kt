package ru.vik.spindel.core.metrics.impl

import kotlinx.coroutines.flow.Flow
import ru.vik.spindel.core.metrics.api.Metric
import ru.vik.spindel.core.metrics.api.MetricsRepository

class MetricsRepositoryImpl : MetricsRepository {
    override fun observeMetrics(brewId: Long): Flow<List<Metric>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshMetrics(): Result<Unit> {
        TODO("Not yet implemented")
    }
}