package ru.vik.spindel.core.database.api

import kotlinx.coroutines.flow.Flow
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.devices.api.Device
import ru.vik.spindel.core.metrics.api.BrewMetricCount
import ru.vik.spindel.core.metrics.api.Metric

interface DatabaseRepository {
    fun observeBrews(): Flow<List<Brew>>
    suspend fun addBrews(list: List<Brew>)
    suspend fun getBrew(brewId: Long): Brew?

    fun observeDevices(): Flow<List<Device>>

    suspend fun addDevice(device: Device)
    suspend fun addDevices(list: List<Device>)

    fun observeMetrics(brewId: Long?): Flow<List<Metric>>
    suspend fun addMetrics(brewId: Long?, list: List<Metric>)
    fun observeCountsByBrew(): Flow<List<BrewMetricCount>>
}