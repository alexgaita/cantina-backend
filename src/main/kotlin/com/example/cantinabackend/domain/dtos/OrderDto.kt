package com.example.cantinabackend.domain.dtos

import kotlinx.serialization.Serializable

@Serializable
data class OrderCreateDto(
    val items: List<OrderItemCreateDto>,
    val comment: String?,
    val discountIds: List<String>,
    val wantSilverware: Boolean,
)

@Serializable
data class OrderItemCreateDto(
    val productId: String,
    val quantity: Int
)