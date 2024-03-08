package com.example.cantinabackend.services

import com.example.cantinabackend.domain.dtos.MenuItemDto
import com.example.cantinabackend.domain.dtos.MenuListDto
import com.example.cantinabackend.domain.dtos.mappers.toDto
import com.example.cantinabackend.domain.dtos.mappers.toViewDto
import com.example.cantinabackend.domain.entities.ItemType
import com.example.cantinabackend.domain.enums.WeekDay
import com.example.cantinabackend.domain.repositories.ContainerRepository
import com.example.cantinabackend.domain.repositories.MenuItemRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

@Service
class MenuItemService(
    private val menuItemRepository: MenuItemRepository,
    private val containerRepository: ContainerRepository
) {

    @Transactional(readOnly = true)
    fun getMenuForDay(): MenuListDto {
        val today = LocalDate.now()
        var viewDate = today.plusDays(1)
        if (today.dayOfWeek.value in listOf(5, 6, 7)) {
            logger.info { "Weekend , showing menu for next week" }
            viewDate = today.plusWeeks(1).with(DayOfWeek.MONDAY)
        }
        val viewDateNumber = WeekDay.valueOf(viewDate.dayOfWeek.name).value
        val menuItems = menuItemRepository.findItemsForDay(viewDateNumber, today)
        return MenuListDto(menuItems.map { it.toViewDto() })
    }

    @Transactional(readOnly = true)
    fun getMenuItemById(id: String): MenuItemDto {
        val menuItem = menuItemRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("Menu not found")
        return menuItem.toDto()
    }

    @Transactional
    fun changeMenuItem(menuItemDto: MenuItemDto) {
        val menuItem =
            menuItemRepository.findByIdOrNull(menuItemDto.name) ?: throw EntityNotFoundException("Menu not found")

        logger.info { "Updating menu item: $menuItem" }

        menuItem.servingSize = menuItemDto.servingSize
        menuItem.normalPrice = menuItemDto.normalPrice
        menuItem.discountedPrice = menuItemDto.discountedPrice
        menuItem.firstPossibleDay = menuItemDto.firstPossibleDay
        menuItem.lastPossibleDay = menuItemDto.lastPossibleDay
        menuItem.photoUrl = menuItemDto.photoUrl
        menuItem.type = menuItemDto.type?.let { ItemType.valueOf(it) }
        menuItem.recurringDays = menuItemDto.recurringDays.sumOf { it.value }

        menuItem.containers.clear()
        menuItem.containers.addAll(containerRepository.findAllById(menuItemDto.containers))

        logger.info { "Updated menu item: $menuItem success" }
    }

}