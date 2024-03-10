package com.example.cantinabackend.domain.dtos.mappers

import com.example.cantinabackend.domain.dtos.MenuItemDto
import com.example.cantinabackend.domain.dtos.MenuItemViewDto
import com.example.cantinabackend.domain.entities.Container
import com.example.cantinabackend.domain.entities.ItemType
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

fun MenuItemDto.toEntity(containers: List<Container>): MenuItem = MenuItem(
    name.trimEnd().trimStart(),
    servingSize,
    normalPrice,
    discountedPrice,
    containers = containers.toMutableList(),
    recurringDays.sumOf { it.value },
    firstPossibleDay,
    lastPossibleDay,
    photoUrl,
    type?.let { ItemType.valueOf(it) }
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
