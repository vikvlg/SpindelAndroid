package ru.vik.spindel.core.database.impl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.vik.spindel.core.database.impl.model.BrewMetricCountTuple
import ru.vik.spindel.core.database.impl.model.MetricDto

@Dao
internal interface MetricDao {
    @Insert
    suspend fun insert(metric: MetricDto)

    @Upsert
    suspend fun upsertAll(list: List<MetricDto>)

    @Query("""
        SELECT * FROM metrics
        WHERE brew_id IS :brewId
        ORDER BY id
        """)
    fun observe(brewId: Long?): Flow<List<MetricDto>>

    @Query("""
        SELECT brew_id, COUNT(id) AS count 
        FROM metrics 
        GROUP BY brew_id
    """)
    fun observeMetricCounts(): Flow<List<BrewMetricCountTuple>>
}
