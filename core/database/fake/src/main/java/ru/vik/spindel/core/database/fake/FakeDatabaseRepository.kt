package ru.vik.spindel.core.database.fake

import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.database.api.DatabaseRepository
import ru.vik.spindel.core.devices.api.Device
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

class FakeDatabaseRepository @Inject constructor() : DatabaseRepository {
    private var brewKey: Long = 0
    private var deviceKey: Long = 0
    private val brewList: MutableList<Brew> = mutableListOf(
        Brew(++brewKey, "Рождественский грог", LocalDate.of(2026, 1, 7)),
        Brew(++brewKey, "Стаут", LocalDate.of(2026, 1, 28)),
        Brew(++brewKey, "Milkshake", LocalDate.of(2026, 2, 14)),
        Brew(++brewKey, "Первомайская варка", LocalDate.of(2026, 5, 1)),
        Brew(++brewKey, "Майская варка", LocalDate.of(2026, 5, 20)),
        Brew(++brewKey, "Быстролагер", LocalDate.of(2026, 6, 3)),
    )
    private val deviceList: List<Device> = listOf(
        Device(++deviceKey, "Первый"),
    )

    private val mutex = Mutex()
    private val brewsState = MutableStateFlow<List<Brew>>(brewList)
    private val devicesState = MutableStateFlow<List<Device>>(deviceList)

    override fun observeBrews(): Flow<List<Brew>> =
        brewsState
            .asStateFlow()
            .onStart {
                delay(500.milliseconds) // Имитация внутренних задержек при работе с локальной БД
            }

    override fun observeDevices(): Flow<List<Device>> =
        devicesState
            .asStateFlow()
            .onStart {
                delay(500.milliseconds) // Имитация внутренних задержек при работе с локальной БД
            }

    override suspend fun addBrews(list: List<Brew>) {
        mutex.withLock {
            val currentList = brewsState.value
            val updatedList = list
                // Простенький фильтр дублей по имени
                .filter { brew ->
                    -1 == currentList.indexOfFirst { it.name == brew.name }
                }.map { brew ->
                    brew.copy(id = ++brewKey)
                }

            brewsState.update { currentList ->
                currentList + updatedList
            }
        }
    }

    override suspend fun addDevice(device: Device) {
        mutex.withLock {
            if (-1 == devicesState.value.indexOfFirst { it.name == device.name })
                devicesState.update { it + device }
        }
    }

    override suspend fun addDevices(list: List<Device>) {
        mutex.withLock {
            val currentList = devicesState.value
            val updatedList = list
                // Простенький фильтр дублей по имени
                .filter { device ->
                    -1 == currentList.indexOfFirst { it.name == device.name }
                }.map { device ->
                    device.copy(id = ++deviceKey)
                }

            devicesState.update { currentList ->
                currentList + updatedList
            }
        }
    }
}