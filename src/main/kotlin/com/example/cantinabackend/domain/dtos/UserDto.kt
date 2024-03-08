package com.example.cantinabackend.domain.dtos

import com.example.cantinabackend.config.annotations.Permission
import jakarta.validation.constraints.Size

data class UserChangeDto(
    val addresses: List<AdressDto>,
    @Size(min = 10, max = 10)
    val phoneNumber: String
)

data class AdressDto(
    val value: String,
    val id: Int
)

data class PermissionDto(
    val permissions: List<Permission>
)