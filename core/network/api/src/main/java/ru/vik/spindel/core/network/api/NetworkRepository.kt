package ru.vik.spindel.core.network.api

import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.devices.api.Device
import ru.vik.spindel.core.metrics.api.Metric

/**
 * Интерфейс репозитория по работе с сетью.
 */
interface NetworkRepository {
    //fun createBrew(): BrewDto
    fun getBrew(brewId: Long): Result<Brew>
    suspend fun getBrewList(): Result<List<Brew>>

    suspend fun getMetricList(brewId: Long?): Result<List<Metric>>
//    fun setMetricBrew(metricId: Long, brewId: Long?)
//    fun setMetricBrew(metricIds: List<Long>, brewId: Long?)
//
//    fun createDevice(): DeviceDto
    fun getDevice(deviceId: Long): Result<Device>
    fun getDeviceList(): Result<List<Device>>
}