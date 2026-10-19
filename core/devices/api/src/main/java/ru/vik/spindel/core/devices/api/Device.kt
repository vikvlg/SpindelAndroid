package ru.vik.spindel.core.devices.api

data class Device(
    val id: Long,
    val name: String,
    val brewId: Long? = null
)