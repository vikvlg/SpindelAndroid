package ru.vik.spindel.core.devices.impl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.vik.spindel.core.devices.api.DeviceRepository

@Module
@InstallIn(SingletonComponent::class)
interface DeviceModule {
    @Binds
    @Singleton
    fun bindDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository
}