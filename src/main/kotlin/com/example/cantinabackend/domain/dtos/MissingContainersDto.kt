package com.example.cantinabackend.domain.dtos

import kotlinx.serialization.Serializable

@Serializable
data class MissingContainersDto(
    val containers: List<String>,
    val items: List<MenuItemDto>,
)