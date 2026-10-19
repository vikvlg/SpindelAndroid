package ru.vik.spindel.core.devices.api

import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun observeDevices(): Flow<List<Device>>
    //fun refreshDevice(deviceId: Long): Result<Device>
    suspend fun refreshDevices(): Result<Unit>
}