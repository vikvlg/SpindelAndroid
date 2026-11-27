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
import ru.vik.spindel.core.metrics.testdata.generateFermentationMetrics
import ru.vik.spindel.core.metrics.testdata.generateTrashMetrics
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
            generateTrashMetrics(startBrewTime, Random(-1))
        } else {
            generateFermentationMetrics(startBrewTime, Random(brewId))
        }
        val state = MutableStateFlow(list)
        metricsState[brewId] = state
        return state
    }

    /** Имитация внутренних задержек при работе с локальной БД. **/
    private suspend fun simulateDbDelay() = delay(2000.milliseconds)
}