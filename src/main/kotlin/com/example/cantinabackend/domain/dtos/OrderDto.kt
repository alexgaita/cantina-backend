@file:UseSerializers(LocalDateSerializer::class, LocalDateTimeSerializer::class)

package com.example.cantinabackend.domain.dtos

import com.example.cantinabackend.domain.dtos.serializers.LocalDateSerializer
import com.example.cantinabackend.domain.dtos.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class OrderCreateDto(
    val items: List<OrderItemCreateDto>,
    val comment: String?,
    val discountIds: List<String>,
    val wantSilverware: Boolean,
    val byCard: Boolean,
    val addressId: Int? = null,
    val phoneNumber: String? = null
)

@Serializable
data class OrdersByDateDto(
    val items: Map<LocalDate, List<OrderViewDto>>
)

@Serializable
data class ContainerOrderDto(
    val containerName: String,
    val quantity: Int,
)

@Serializable
data class OrderViewDto(
    val id: Int,
    val items: List<OrderItemViewDto>,
    val containers: List<ContainerOrderDto>,
    val comment: String?,
    val discountIds: List<String>,
    val address: String,
    val phoneNumber: String?,
    val status: String,
    val createdAt: LocalDateTime,
    val total: Double,
    val requiresPayment: Boolean
)

@Serializable
data class OrderItemViewDto(
    val productId: String,
    val quantity: Int,
    val price: Double
)

@Serializable
data class OrderItemCreateDto(
    val productId: String,
    val quantity: Int
)

