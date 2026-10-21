package ru.vik.spindel.core.network.fake

import jakarta.inject.Inject
import kotlinx.coroutines.delay
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.devices.api.Device
import ru.vik.spindel.core.network.api.NetworkRepository
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

class FakeNetworkRepository @Inject constructor() : NetworkRepository {
    private var brewKey: Long = 0
    private var deviceKey: Long = 0
    private val brewList: List<Brew> = listOf(
        Brew(++brewKey, "Светлый эль", LocalDate.of(2026, 7, 1)),
        Brew(++brewKey, "IPA осенний", LocalDate.of(2026, 9, 18)),
        Brew(++brewKey, "Тыквенный эль", LocalDate.of(2026, 10, 1)),
    )
    private val deviceList: List<Device> = listOf(
        Device(++deviceKey, "Второй"),
    )

    override fun getBrew(brewId: Long): Result<Brew> {
        val brew = brewList.find { it.id == brewId }
        return if (brew != null)
            Result.success(brew)
        else
            Result.failure(Exception("Не найдено"))
    }

    override suspend fun getBrewList(): Result<List<Brew>> {
        delay(1500.milliseconds) // Имитация задержки сети
        return Result.success(brewList)
    }

    override fun getDevice(deviceId: Long): Result<Device> {
        val device = deviceList.find { it.id == deviceId }
        return if (device != null)
            Result.success(device)
        else
            Result.failure(Exception("Не найдено"))
    }

    override fun getDeviceList(): Result<List<Device>> {
        return Result.success(deviceList)
    }
}