package ru.vik.spindel.core.database.fake

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import ru.vik.spindel.core.database.api.DatabaseRepository

@Module
@InstallIn(SingletonComponent::class)
interface FakeDatabaseModule {
    @Binds
    @Singleton
    fun bindDatabaseRepository(impl: FakeDatabaseRepository): DatabaseRepository
}