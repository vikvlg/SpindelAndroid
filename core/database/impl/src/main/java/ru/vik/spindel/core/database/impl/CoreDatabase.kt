package ru.vik.spindel.core.database.impl

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.vik.spindel.core.database.impl.model.BrewDto
import ru.vik.spindel.core.database.impl.model.DeviceDto
import ru.vik.spindel.core.database.impl.model.MetricDto

/** Публичный конфиг БД. **/
interface CoreDatabaseConfig {
    val dbName: String
    val isDebug: Boolean
}

@Database(
    version = CoreDatabase.DATABASE_VERSION,
    entities = [
        BrewDto::class,
        DeviceDto::class,
        MetricDto::class,
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
internal abstract class CoreDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_VERSION = 1
    }

    abstract fun getBrewDao(): BrewDao
    abstract fun getDeviceDao(): DeviceDao
    abstract fun getMetricDao(): MetricDao
}

internal fun createCoreDatabase(
    context: Context,
    config: CoreDatabaseConfig
): CoreDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        CoreDatabase::class.java,
        config.dbName
    ).apply {
        if (config.isDebug) {
            fallbackToDestructiveMigration(dropAllTables = true)
        }
    }
    .addMigrations(*ALL_MIGRATIONS)
    .build()
}
