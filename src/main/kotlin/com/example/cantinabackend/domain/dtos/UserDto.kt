package com.example.cantinabackend.domain.dtos

import com.example.cantinabackend.config.annotations.Permission
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

data class UserChangeDto(
    val addresses: List<AddressDto>,
    @Size(min = 10, max = 10)
    val phoneNumber: String
)

@Serializable
data class UserDto(
    @Size(min = 10, max = 10)
    val phoneNumber: String?,
    val addresses: List<AddressDto>,
)

@Serializable
data class AddressDto(
    val id: Int,
    val value: String,
    val isCurrent: Boolean
)

data class PermissionDto(
    val permissions: List<Permission>
)