package ru.vik.spindel.core.brews.api

import kotlinx.coroutines.flow.Flow

interface BrewRepository {
    fun observeBrews(): Flow<List<Brew>>
    //fun refreshBrew(brewId: Long): Result<Brew>
    suspend fun refreshBrews(): Result<Unit>
}