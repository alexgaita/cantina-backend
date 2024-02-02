package com.example.cantinabackend.web

import com.example.cantinabackend.domain.entities.MenuItem
import com.example.cantinabackend.domain.enums.WeekDay
import com.example.cantinabackend.domain.repositories.DailyMenuRepository
import com.example.cantinabackend.domain.repositories.MenuItemRepository
import com.example.cantinabackend.services.XCelReaderService
import com.example.cantinabackend.web.swagger.IMenuController
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.DayOfWeek
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

@RestController
class MenuController(
    private var menuItemRepository: MenuItemRepository,
    private var xCelReaderService: XCelReaderService,
    private val dailyMenuRepository: DailyMenuRepository
) : IMenuController {

    @GetMapping("/menu")
    fun getMenu(): List<MenuItem> {

        var today = LocalDate.now().plusDays(1)
        if (today.dayOfWeek.value in listOf(6, 7)) {
            logger.info { "Weekend , showing menu for next week" }
            today = today.plusWeeks(1).with(DayOfWeek.MONDAY)
        }

        val todayAsNumber = WeekDay.valueOf(today.dayOfWeek.name).value

        val menuItems = menuItemRepository.findItemsForDay(todayAsNumber, today)

        return menuItems
    }

    @PostMapping("/upload", consumes = ["multipart/form-data"])
    override fun handleFileUpload(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        try {
            // Read the Excel file using Apache POI
            val workbook = WorkbookFactory.create(file.inputStream)

            xCelReaderService.readMenu(workbook)
            // Do something with the workbook, e.g., process the data

            // Return a success response
            return ResponseEntity.status(200).body("File uploaded successfully!")
        } catch (e: Exception) {
            // Handle exceptions, e.g., file format issues, processing errors
            return ResponseEntity.status(500).body("Error uploading the file: ${e.message}")
        }
    }
}