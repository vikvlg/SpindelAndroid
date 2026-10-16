package ru.vik.spindel.core.network.fake

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.vik.spindel.core.network.api.NetworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FakeNetworkModule {
    @Binds
    @Singleton
    fun bindNetworkRepository(impl: FakeNetworkRepository): NetworkRepository
}