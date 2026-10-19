package ru.vik.spindel.core.devices.impl

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.vik.spindel.core.devices.api.Device
import ru.vik.spindel.core.devices.api.DeviceRepository
import ru.vik.spindel.core.network.api.NetworkRepository

class DeviceRepositoryImpl @Inject constructor(
    private val network: NetworkRepository
) : DeviceRepository {
    override fun observeDevices(): Flow<List<Device>> = flow {
        // TODO: Доставать данные из БД?
        network.getDeviceList().onSuccess {
            emit(it)
        }.onFailure {  }
    }

    override suspend fun refreshDevices(): Result<Unit> {
        // TODO: Запрос в сеть.
        return Result.success(Unit)
    }
}