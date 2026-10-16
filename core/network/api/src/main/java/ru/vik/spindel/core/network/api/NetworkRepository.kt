package ru.vik.spindel.core.network.api

/**
 * Интерфейс репозитория по работе с сетью.
 */
interface NetworkRepository {
    //fun createBrew(): BrewDto
    fun getBrew(brewId: Long): Result<BrewDto>
    fun getBrewList(): Result<List<BrewDto>>

//    fun getMetricList(brewId: Long?): List<MetricDto>
//    fun setMetricBrew(metricId: Long, brewId: Long?)
//    fun setMetricBrew(metricIds: List<Long>, brewId: Long?)
//
//    fun createDevice(): DeviceDto
//    fun getDevice(deviceId: Long): DeviceDto
//    fun getDeviceList(): List<DeviceDto>
}