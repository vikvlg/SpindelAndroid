package ru.vik.spindel.core.database.impl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.vik.spindel.core.database.impl.model.DeviceDto

@Dao
interface DeviceDao {
    @Insert
    suspend fun insert(device: DeviceDto)

    @Upsert
    suspend fun upsertAll(list: List<DeviceDto>)

    @Query("""
        SELECT * FROM devices
        ORDER BY id
    """)
    fun observeAll(): Flow<List<DeviceDto>>
}
