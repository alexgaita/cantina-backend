package com.example.cantinabackend.web

import com.example.cantinabackend.config.annotations.Permission
import com.example.cantinabackend.config.annotations.RequiredPermissions
import com.example.cantinabackend.domain.dtos.MenuItemDto
import com.example.cantinabackend.domain.dtos.MenuItemEditDto
import com.example.cantinabackend.domain.dtos.MenuListDto
import com.example.cantinabackend.domain.dtos.MissingContainersDto
import com.example.cantinabackend.services.MenuItemService
import com.example.cantinabackend.services.XCelReaderService
import com.example.cantinabackend.web.swagger.IMenuController
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

private val logger = KotlinLogging.logger {}

@RestController
class MenuController(
    private val menuItemService: MenuItemService,
    private var xCelReaderService: XCelReaderService,
) : IMenuController {

    @GetMapping("/menu")
    @RequiredPermissions([Permission.NORMAL_USER])
    override fun getMenu(): MenuListDto = menuItemService.getMenuForDay()

    @GetMapping("/menu/{id}")
    @RequiredPermissions([Permission.ADMIN])
    override fun getMenuById(@PathVariable id: String): MenuItemEditDto = menuItemService.getMenuItemById(id)

    @PutMapping("/menu")
    @RequiredPermissions([Permission.ADMIN])
    override fun createOrUpdateMenuItem(@RequestBody menuItemDto: MenuItemDto) =
        menuItemService.createOrUpdateMenuItem(menuItemDto)

    @DeleteMapping("/menu/{id}")
    @RequiredPermissions([Permission.ADMIN])
    override fun deleteMenuItem(@PathVariable id: String) = menuItemService.deleteMenuItem(id)

    @PostMapping("/upload", consumes = ["multipart/form-data"])
    @RequiredPermissions([Permission.ADMIN])
    override fun handleFileUpload(@RequestParam("file") file: MultipartFile): MissingContainersDto {

        // Read the Excel file using Apache POI
        val workbook = WorkbookFactory.create(file.inputStream)

        return xCelReaderService.readMenu(workbook)
    }
}