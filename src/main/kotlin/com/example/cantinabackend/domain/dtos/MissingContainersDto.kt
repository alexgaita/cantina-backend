package com.example.cantinabackend.domain.dtos

import kotlinx.serialization.Serializable

@Serializable
data class MissingContainersDto(
    val containers: List<String>,
    val items: List<MenuItemDto>,
)

@Serializable
data class ContainerDto(
    val name: String,
    val price: Double,
)

@Serializable
data class ItemContainerDto(
    val items: Map<String, List<ContainerDto>>,
)