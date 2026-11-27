package ru.vik.spindel.core.database.fake

import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.vik.spindel.core.brews.api.Brew
import ru.vik.spindel.core.database.api.DatabaseRepository
import ru.vik.spindel.core.devices.api.Device
import ru.vik.spindel.core.metrics.api.BrewMetricCount
import ru.vik.spindel.core.metrics.api.Metric
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class FakeDatabaseRepository @Inject constructor() : DatabaseRepository {
    private var brewKey: Long = 0
    private var deviceKey: Long = 0
    private var metricKey: Long = 0
    private val brewList: MutableList<Brew> = mutableListOf(
        Brew(++brewKey, "Рождественский грог", LocalDateTime.of(2026, 1, 7, 12, 0, 0), false),
        Brew(++brewKey, "Стаут", LocalDateTime.of(2026, 1, 28, 12, 0, 0), false),
        Brew(++brewKey, "Milkshake", LocalDateTime.of(2026, 2, 14, 12, 0, 0), false),
        Brew(++brewKey, "Первомайская варка", LocalDateTime.of(2026, 5, 1, 12, 0, 0), false),
        Brew(++brewKey, "Майская варка", LocalDateTime.of(2026, 5, 20, 12, 0, 0), false),
        Brew(++brewKey, "Быстролагер", LocalDateTime.of(2026, 6, 3, 12, 0, 0), false),
    )
    private val deviceList: List<Device> = listOf(
        Device(++deviceKey, "Первый"),
    )

    private val mutex = Mutex()
    private val brewsState = MutableStateFlow<List<Brew>>(brewList)
    private val devicesState = MutableStateFlow<List<Device>>(deviceList)
    private val metricsState: MutableMap<Long?, MutableStateFlow<List<Metric>>> = mutableMapOf() //MutableStateFlow<List<Metric>>(brewList)

    init {
        addNewMetrics(brewId = null)
    }

    override fun observeBrews(): Flow<List<Brew>> =
        brewsState
            .asStateFlow()
            .onStart {
                simulateDbDelay()
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

    override suspend fun getBrew(brewId: Long): Brew? {
        mutex.withLock {
            simulateDbDelay()
            return brewsState.value.find { brew -> brew.id == brewId }
        }
    }

    override fun observeDevices(): Flow<List<Device>> =
        devicesState
            .asStateFlow()
            .onStart {
                simulateDbDelay()
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

    override fun observeMetrics(brewId: Long?): Flow<List<Metric>> {
        var state = metricsState[brewId]
        if (state == null) {
            // BUG: Тут в теории может быть ошибка из-за прямого обращения к brewsState.value без Mutex.
            val brew = brewsState.value.find { brew -> brew.id == brewId }
            state = addNewMetrics(brewId, brew?.date)
        }

        return state
            .asStateFlow()
            .onStart {
                simulateDbDelay()
            }
    }

    override suspend fun addMetrics(brewId: Long?, list: List<Metric>) {
        // TODO: .
    }

    override fun observeCountsByBrew(): Flow<List<BrewMetricCount>> {
        return brewsState
            .asStateFlow()
            .onStart {
                simulateDbDelay()
            }
            .map { list ->
                list.map { brew ->
                    val state = metricsState[brew.id] ?: addNewMetrics(brew.id, brew.date)
                    BrewMetricCount(brew.id, state.value.size)
                } + BrewMetricCount(null, metricsState[null]?.value?.size ?: 0)
            }
    }

    private fun addNewMetrics(brewId: Long?, startTime: LocalDateTime? = null): MutableStateFlow<List<Metric>> {
        val startBrewTime = startTime ?: LocalDateTime.now()
        val list = if (brewId == null) {
            generateTrashMetrics(startBrewTime)
        } else {
            generateMetrics(startBrewTime)
        }
        val state = MutableStateFlow(list)
        metricsState[brewId] = state
        return state
    }

    private fun generateTrashMetrics(startTime: LocalDateTime = LocalDateTime.now()): List<Metric> {
        val count = Random.nextInt(3, 10)
        var time = startTime.minusDays(7)
        return buildList {
            for (i in 0..<count) {
                add(
                    Metric(
                        id = i + 1L,
                        date = time,
                        gravity = Random.nextDouble(5.0, 25.0),
                        temperature = Random.nextDouble(10.0, 45.0),
                    )
                )
                time = time.plusMinutes(Random.nextLong(55, 65))
            }
        }
    }

    private fun generateMetrics(startTime: LocalDateTime = LocalDateTime.now()): List<Metric> {
        // Параметры брожения
        val startGravity = Random.nextDouble(10.0, 16.0)
        val attenuation = Random.nextDouble(70.0, 90.0)
        val duration = Random.nextInt(10, 21)
        val startTemperature = Random.nextDouble(22.0, 30.0)
        val targetTemperature = Random.nextDouble(10.0, 18.0)

        return generateFermentationMetrics(
            startTime,
            startGravity,
            attenuation,
            duration,
            startTemperature,
            targetTemperature)
    }

    /** Имитация внутренних задержек при работе с локальной БД. **/
    private suspend fun simulateDbDelay() = delay(2000.milliseconds)
}