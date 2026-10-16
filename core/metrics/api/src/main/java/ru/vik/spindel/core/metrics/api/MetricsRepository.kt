package ru.vik.spindel.core.metrics.api

import kotlinx.coroutines.flow.Flow

interface MetricsRepository {
    fun observeMetrics(brewId: Long): Flow<List<Metric>>
    suspend fun refreshMetrics(): Result<Unit>
}
