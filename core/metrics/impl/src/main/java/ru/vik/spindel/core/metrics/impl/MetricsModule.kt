package ru.vik.spindel.core.metrics.impl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.vik.spindel.core.metrics.api.MetricsRepository

@Module
@InstallIn(SingletonComponent::class)
interface MetricsModule {
    @Binds
    @Singleton
    fun bindMetricsRepository(impl: MetricsRepositoryImpl): MetricsRepository
}