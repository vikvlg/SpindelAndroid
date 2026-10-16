package ru.vik.spindel.core.network.fake

import ru.vik.spindel.core.network.api.BrewDto
import ru.vik.spindel.core.network.api.NetworkRepository

class FakeNetworkRepository : NetworkRepository {
    override fun getBrew(brewId: Long): Result<BrewDto> {
        return Result.success(BrewDto(1))
    }

    override fun getBrewList(): Result<List<BrewDto>> {
        return Result.success(emptyList())
    }
}