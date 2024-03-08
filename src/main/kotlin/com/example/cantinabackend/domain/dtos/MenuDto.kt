@file:UseSerializers(LocalDateSerializer::class)

package com.example.cantinabackend.domain.dtos

import com.example.cantinabackend.domain.dtos.serializers.LocalDateSerializer
import com.example.cantinabackend.domain.entities.MenuItem
import com.example.cantinabackend.domain.enums.WeekDay
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDate

@Serializable
data class MenuListDto(
    val items: List<MenuItemViewDto>
)

data class MenuItemEditDto(
    val menuItem: MenuItem,
    val recurringDays: List<WeekDay>,
    val possibleContainers: List<String>
)

@Serializable
data class MenuItemDto(
    val name: String,
    val servingSize: String,
    val normalPrice: Double,
    val discountedPrice: Double,
    val containers: List<String>,
    val recurringDays: List<WeekDay>,
    val firstPossibleDay: LocalDate,
    val lastPossibleDay: LocalDate,
    val photoUrl: String?,
    val type: String?,
)

@Serializable
data class MenuItemViewDto(
    val name: String,
    val servingSize: String,
    val normalPrice: Double,
    val discountedPrice: Double,
    val firstPossibleDay: LocalDate,
    val lastPossibleDay: LocalDate,
    val photoUrl: String?,
    val type: String?,
)
