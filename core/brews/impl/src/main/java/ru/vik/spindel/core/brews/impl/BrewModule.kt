package ru.vik.spindel.core.brews.impl

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.vik.spindel.core.brews.api.BrewRepository

@Module
@InstallIn(SingletonComponent::class)
interface BrewModule {
    @Binds
    @Singleton
    fun bindBrewRepository(impl: BrewRepositoryImpl): BrewRepository
}