package ru.vik.spindel.core.database.impl

import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.database.impl.model.BrewDto
import ru.vik.spindel.core.database.impl.model.BrewMetricCountTuple
import ru.vik.spindel.core.database.impl.model.DeviceDto
import ru.vik.spindel.core.database.impl.model.MetricDto
import ru.vik.spindel.core.devices.api.Device
import ru.vik.spindel.core.metrics.api.BrewMetricCount
import ru.vik.spindel.core.metrics.api.Metric

internal fun MetricDto.toMetricDomain(): Metric {
    return Metric(id, date, gravity, temperature)
}

internal fun List<MetricDto>.toMetricDomain(): List<Metric> = map(MetricDto::toMetricDomain)

internal fun Metric.toMetricDto(brewId: Long): MetricDto {
    return MetricDto(id, date, brewId, gravity, temperature)
}

internal fun List<Metric>.toMetricDto(brewId: Long): List<MetricDto> = map { it.toMetricDto(brewId) }


internal fun DeviceDto.toDeviceDomain(): Device {
    return Device(id, name, brewId)
}

internal fun List<DeviceDto>.toDeviceDomain(): List<Device> = map(DeviceDto::toDeviceDomain)

internal fun Device.toDeviceDto(): DeviceDto {
    return DeviceDto(id, name, brewId)
}

internal fun BrewDto.toBrewDomain(): Brew {
    return Brew(id, name, date, isActive)
}

internal fun List<BrewDto>.toBrewDomain(): List<Brew> = map(BrewDto::toBrewDomain)

internal fun Brew.toBrewDto(): BrewDto {
    return BrewDto(id, name, date, isActive)
}

internal fun BrewMetricCountTuple.toBrewMetricCountDomain(): BrewMetricCount {
    return BrewMetricCount(brewId, count)
}

internal fun List<BrewMetricCountTuple>.toBrewMetricCountDomain(): List<BrewMetricCount> = map(BrewMetricCountTuple::toBrewMetricCountDomain)
