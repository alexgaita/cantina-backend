package com.example.cantinabackend.domain.dtos

import com.example.cantinabackend.config.annotations.Permission
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable
import java.util.*

data class UserChangeDto(
    @Size(min = 10, max = 10)
    @Pattern(regexp = "^[0-9]+$", message = "Should only contain numeric characters")
    val phoneNumber: String
)

@Serializable
data class UserDto(
    @Size(min = 10, max = 10)
    val phoneNumber: String?,
    val addresses: List<AddressDto>,
)

@Serializable
data class CartelaOrderDto(
    val amount: Int,
    val quantity: Int
)

data class CartelaPaymentDto(
    val amount: Int,
    val quantity: Int,
    val userId: UUID
)

@Serializable
data class AddressDto(
    val id: Int,
    val value: String,
    val isCurrent: Boolean
)

@Serializable
data class CartelaDto(
    val id: String,
    val value: Double,
    val isActive: Boolean
)

@Serializable
data class PermissionDto(
    val permissions: List<Permission>,
    val cartele: List<CartelaDto>
)