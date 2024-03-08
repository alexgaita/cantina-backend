package com.example.cantinabackend.domain.dtos.mappers

import com.example.cantinabackend.domain.dtos.MenuItemDto
import com.example.cantinabackend.domain.dtos.MenuItemViewDto
import com.example.cantinabackend.domain.entities.MenuItem

fun MenuItem.toDto(): MenuItemDto = MenuItemDto(
    name,
    servingSize,
    normalPrice,
    discountedPrice,
    containers.map { it.name },
    recurringDays = computeRecurringDays(),
    firstPossibleDay,
    lastPossibleDay,
    photoUrl,
    type?.name
)

fun MenuItem.toViewDto(): MenuItemViewDto = MenuItemViewDto(
    name,
    servingSize,
    normalPrice,
    discountedPrice,
    firstPossibleDay,
    lastPossibleDay,
    photoUrl,
    type?.name
)
