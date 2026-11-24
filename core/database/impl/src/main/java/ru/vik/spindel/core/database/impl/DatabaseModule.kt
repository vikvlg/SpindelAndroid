package ru.vik.spindel.core.database.impl

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.vik.spindel.core.database.api.DatabaseRepository

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context, config: CoreDatabaseConfig): CoreDatabase = createCoreDatabase(context, config)

    @Provides
    fun provideBrewDao(database: CoreDatabase): BrewDao = database.getBrewDao()

    @Provides
    fun provideDeviceDao(database: CoreDatabase): DeviceDao = database.getDeviceDao()

    @Provides
    fun provideMetricDao(database: CoreDatabase): MetricDao = database.getMetricDao()
}

@Module
@InstallIn(SingletonComponent::class)
internal interface DatabaseBindsModule {
    @Singleton
    @Binds
    fun bindDatabaseRepository(impl: DatabaseRepositoryImpl): DatabaseRepository
}
