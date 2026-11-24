package ru.vik.spindel

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.vik.spindel.core.database.impl.CoreDatabaseConfig

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {
    @Singleton
    @Provides
    fun provideDatabaseConfig(): CoreDatabaseConfig = object : CoreDatabaseConfig {
        override val dbName: String = BuildConfig.DB_NAME
        override val isDebug: Boolean = BuildConfig.DEBUG
    }
}