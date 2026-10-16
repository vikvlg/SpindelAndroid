package ru.vik.spindel.core.network.api

import ru.vik.spindel.core.brews.api.Brew

/**
 * Интерфейс репозитория по работе с сетью.
 */
interface NetworkRepository {
    //fun createBrew(): BrewDto
    fun getBrew(brewId: Long): Result<Brew>
    fun getBrewList(): Result<List<Brew>>

//    fun getMetricList(brewId: Long?): List<MetricDto>
//    fun setMetricBrew(metricId: Long, brewId: Long?)
//    fun setMetricBrew(metricIds: List<Long>, brewId: Long?)
//
//    fun createDevice(): DeviceDto
//    fun getDevice(deviceId: Long): DeviceDto
//    fun getDeviceList(): List<DeviceDto>
}