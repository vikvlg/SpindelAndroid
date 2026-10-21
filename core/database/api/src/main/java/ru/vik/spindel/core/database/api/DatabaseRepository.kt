package ru.vik.spindel.core.database.api

import kotlinx.coroutines.flow.Flow
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.devices.api.Device

interface DatabaseRepository {
    fun observeBrews(): Flow<List<Brew>>
    fun observeDevices(): Flow<List<Device>>
    suspend fun addBrews(list: List<Brew>)
    suspend fun addDevice(device: Device)
    suspend fun addDevices(list: List<Device>)
}