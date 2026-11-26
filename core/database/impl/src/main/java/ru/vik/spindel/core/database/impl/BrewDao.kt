package ru.vik.spindel.core.database.impl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.vik.spindel.core.database.impl.model.BrewDto

@Dao
internal interface BrewDao {
    @Insert
    suspend fun insert(brew: BrewDto)

    @Upsert
    suspend fun upsertAll(list: List<BrewDto>)

    @Query("""
        SELECT * from brews
        WHERE id = :id
    """)
    suspend fun get(id: Long): BrewDto?

    @Query("""
        SELECT * from brews
        ORDER BY date
    """)
    fun observeAll(): Flow<List<BrewDto>>
}
