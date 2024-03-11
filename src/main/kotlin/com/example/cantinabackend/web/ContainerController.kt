package com.example.cantinabackend.web

import com.example.cantinabackend.config.annotations.Permission
import com.example.cantinabackend.config.annotations.RequiredPermissions
import com.example.cantinabackend.domain.dtos.ContainerDto
import com.example.cantinabackend.domain.dtos.ItemContainerDto
import com.example.cantinabackend.domain.repositories.ContainerRepository
import com.example.cantinabackend.domain.repositories.MenuItemRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/containers")
@Validated
class ContainerController(
    private val containerRepository: ContainerRepository,
    private val menuItemRepository: MenuItemRepository
) {

    @GetMapping()
    @RequiredPermissions([Permission.ADMIN])
    @Transactional
    fun getContainers(): List<String> = containerRepository.findAll().map { it.name }

    @PostMapping("/items")
    @RequiredPermissions([Permission.NORMAL_USER])
    @Transactional
    fun getContainersForItems(@RequestBody items: List<String>): ItemContainerDto {
        val menuItems = menuItemRepository.findAllById(items)
        if (menuItems.size != items.size) {
            throw IllegalArgumentException("Some items were not found")
        }

        val menuItemsMap = menuItems.associateBy({ it.name },
            { it.containers.map { container -> ContainerDto(container.name, container.price) } })

        return ItemContainerDto(menuItemsMap)
    }
}