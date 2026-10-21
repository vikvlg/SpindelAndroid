package ru.vik.spindel.core.devices.impl

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import ru.vik.spindel.core.database.api.DatabaseRepository
import ru.vik.spindel.core.devices.api.Device
import ru.vik.spindel.core.devices.api.DeviceRepository
import ru.vik.spindel.core.network.api.NetworkRepository

class DeviceRepositoryImpl @Inject constructor(
    private val database: DatabaseRepository,
    private val network: NetworkRepository
) : DeviceRepository {
    override fun observeDevices(): Flow<List<Device>> = database.observeDevices()

    override suspend fun refreshDevices(): Result<Unit> {
        return network.getDeviceList().mapCatching { list ->
            database.addDevices(list)
        }
    }
}