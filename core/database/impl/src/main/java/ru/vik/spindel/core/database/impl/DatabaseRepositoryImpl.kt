package ru.vik.spindel.core.database.impl

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.database.api.DatabaseRepository
import ru.vik.spindel.core.database.impl.model.BrewDto
import ru.vik.spindel.core.database.impl.model.BrewMetricCountTuple
import ru.vik.spindel.core.database.impl.model.DeviceDto
import ru.vik.spindel.core.database.impl.model.MetricDto
import ru.vik.spindel.core.devices.api.Device
import ru.vik.spindel.core.metrics.api.BrewMetricCount
import ru.vik.spindel.core.metrics.api.Metric

class DatabaseRepositoryImpl @Inject internal constructor(
    private val brewDao: BrewDao,
    private val deviceDao: DeviceDao,
    private val metricDao: MetricDao,
) : DatabaseRepository {
    override fun observeBrews(): Flow<List<Brew>> {
        return brewDao.observeAll().map(List<BrewDto>::toBrewDomain)
    }

    override suspend fun addBrews(list: List<Brew>) {
        val dtoList = list.map { it.toBrewDto() }
        brewDao.upsertAll(dtoList)
    }

    override suspend fun getBrew(brewId: Long): Brew? {
        return brewDao.get(brewId)?.toBrewDomain()
    }

    override fun observeDevices(): Flow<List<Device>> {
        return deviceDao.observeAll().map(List<DeviceDto>::toDeviceDomain)
    }

    override suspend fun addDevice(device: Device) {
        deviceDao.insert(device.toDeviceDto())
    }

    override suspend fun addDevices(list: List<Device>) {
        val dtoList = list.map { it.toDeviceDto() }
        deviceDao.upsertAll(dtoList)
    }

    override fun observeMetrics(brewId: Long?): Flow<List<Metric>> {
        return metricDao.observe(brewId).map (List<MetricDto>::toMetricDomain )
    }

    override suspend fun addMetrics(
        brewId: Long,
        list: List<Metric>
    ) {
        val dtoList = list.map { it.toMetricDto(brewId) }
        metricDao.upsertAll(dtoList)
    }

    override fun observeCountsByBrew(): Flow<List<BrewMetricCount>> {
        return metricDao.observeMetricCounts().map(List<BrewMetricCountTuple>::toBrewMetricCountDomain)
    }
}
