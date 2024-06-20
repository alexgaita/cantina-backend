package com.example.cantinabackend.services

import com.example.cantinabackend.domain.dtos.MenuItemDto
import com.example.cantinabackend.domain.dtos.mappers.toEntity
import com.example.cantinabackend.domain.enums.WeekDay
import com.example.cantinabackend.domain.repositories.ContainerRepository
import com.example.cantinabackend.domain.repositories.MenuItemRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate

class MenuItemServiceTest {

    private val menuItemRepository: MenuItemRepository = mockk()
    private val containerRepository: ContainerRepository = mockk()

    private val menuItemService = MenuItemService(menuItemRepository, containerRepository)

    private fun createMenuItemDto() = MenuItemDto(
        "name", "kg", 0.0, 0.0, emptyList(), emptyList(), LocalDate.now(),
        LocalDate.now(), "", null
    )

    @Test
    fun `when creating menu item with empty name then throw IllegalArgumentException`() {
        //ARRANGE
        val menuItemDto = createMenuItemDto().copy(name = "")

        //ACT & ASSERT
        val error = assertThrows<IllegalArgumentException> {
            menuItemService.createOrUpdateMenuItem(menuItemDto)
        }
        assertEquals("Name cannot be empty", error.message)
    }

    @Test
    fun `when creating menu item with empty recurring days then throw IllegalArgumentException`() {
        //ARRANGE
        val menuItemDto = createMenuItemDto().copy(recurringDays = emptyList())

        //ACT & ASSERT
        val error = assertThrows<IllegalArgumentException> {
            menuItemService.createOrUpdateMenuItem(menuItemDto)
        }
        assertEquals("Recurring days cannot be empty", error.message)
    }

    @Test
    fun `when creating manu item and first possible day is after the last possible day then throw IllegalArgumentException`() {
        //ARRANGE
        val menuItemDto = createMenuItemDto().copy(
            recurringDays = listOf(WeekDay.MONDAY),
            firstPossibleDay = LocalDate.now().plusDays(1),
            lastPossibleDay = LocalDate.now()
        )

        //ACT & ASSERT
        val error = assertThrows<IllegalArgumentException> {
            menuItemService.createOrUpdateMenuItem(menuItemDto)
        }

        assertEquals("First possible day cannot be after last possible day", error.message)
    }

    @Test
    fun `when creating menu item and everything is ok then do not throw any exception`() {
        //ARRANGE
        val menuItemDto = createMenuItemDto().copy(recurringDays = listOf(WeekDay.MONDAY))

        every { menuItemRepository.findByIdOrNull(menuItemDto.name) } returns null
        every { containerRepository.findAllById(any()) } returns emptyList()
        every { menuItemRepository.save(any()) } answers { firstArg() }

        //ACT & ASSERT
        assertDoesNotThrow { menuItemService.createOrUpdateMenuItem(menuItemDto) }
    }

    @Test
    fun `when updating menu item and everything is ok then change the object and don't throw error`() {
        //ARRANGE
        val menuItemDto = createMenuItemDto().copy(recurringDays = listOf(WeekDay.MONDAY))

        every { menuItemRepository.findByIdOrNull(menuItemDto.name) } returns menuItemDto.toEntity(emptyList())
        every { containerRepository.findAllById(any()) } returns emptyList()
        every { menuItemRepository.save(any()) } answers { firstArg() }

        //ACT & ASSERT
        assertDoesNotThrow { menuItemService.createOrUpdateMenuItem(menuItemDto) }
    }

}