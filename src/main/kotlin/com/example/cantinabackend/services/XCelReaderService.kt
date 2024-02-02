package com.example.cantinabackend.services

import com.example.cantinabackend.domain.entities.Container
import com.example.cantinabackend.domain.entities.DailyMenu
import com.example.cantinabackend.domain.entities.MenuItem
import com.example.cantinabackend.domain.repositories.ContainerRepository
import com.example.cantinabackend.domain.repositories.MenuItemRepository
import jakarta.transaction.Transactional
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class XCelReaderService(
    private val menuItemRepository: MenuItemRepository,
    private val containerRepository: ContainerRepository
) {

    @Transactional
    fun readMenu(workbook: Workbook) {

        var menuInterval: Pair<LocalDate, LocalDate>? = null
        val menuItemsList = mutableListOf<MenuItem>()
        val containerList = mutableListOf<Container>()
        val dailyMenuList = mutableListOf<DailyMenu>()

        for (day in 0..4) {
            val sheet = workbook.getSheetAt(day)
            if (menuInterval == null) {
                menuInterval = readMenuInterval(sheet)
            }

            val (startDate, endDate) = menuInterval

            dailyMenuList.addAll(calculateDailyMenuItems(sheet, day, endDate))
            menuItemsList.addAll(calculateMenuItems(sheet, day, endDate))
            containerList.addAll(calculateContainers(sheet))
        }

        saveMenuItems(menuItemsList)
        containerRepository.saveAll(containerList.distinctBy { it.name })
    }

    private fun readMenuInterval(sheet: Sheet): Pair<LocalDate, LocalDate> {
        val row = sheet.getRow(1)
        val cell = row.getCell(0)
        return formatMenuInterval(cell.stringCellValue)
    }

    private fun formatMenuInterval(dateString: String): Pair<LocalDate, LocalDate> {
        val regexPattern = """(\d{2}\.\d{2}\.\d{4})\s*-\s*(\d{2}\.\d{2}\.\d{4})""".toRegex()
        val matchResult = regexPattern.find(dateString)

        if (matchResult != null) {
            val startDateString = matchResult.groupValues[1]
            val endDateString = matchResult.groupValues[2]

            val startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            val endDate = LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("dd.MM.yyyy"))

            return Pair(startDate, endDate)

        } else {
            throw Exception("Invalid date format")
        }
    }

    private fun handleGetPrice(cell: Cell): Double {
        return when (cell.cellType) {
            CellType.NUMERIC -> cell.numericCellValue
            CellType.STRING -> cell.stringCellValue.let { priceString ->
                if (priceString.contains(" lei")) {
                    priceString.replace(" lei", "").replace(",", ".").toDouble()
                } else {
                    throw Exception("Invalid price format")
                }
            }

            else -> throw Exception("Invalid price format")
        }
    }

    private fun calculateDailyMenuItems(
        sheet: Sheet,
        day: Int,
        endDate: LocalDate,
    ): List<DailyMenu> = (9..27).toList().map { rowNumber ->
        val row = sheet.getRow(rowNumber)

        val itemName = row.getCell(1).stringCellValue
        val discountedPrice = handleGetPrice(row.getCell(2))
        val normalPrice = handleGetPrice(row.getCell(3))
        val servingSize = row.getCell(4).stringCellValue

        DailyMenu(
            description = itemName,
            recurringDays = 1,
            lastPosibleDay = endDate
        )
    }

    private fun calculateMenuItems(
        sheet: Sheet,
        day: Int,
        endDate: LocalDate,
    ): List<MenuItem> = (9..27).toList().map { rowNumber ->
        val row = sheet.getRow(rowNumber)

        val itemName = row.getCell(1).stringCellValue
        val discountedPrice = handleGetPrice(row.getCell(2))
        val normalPrice = handleGetPrice(row.getCell(3))
        val servingSize = row.getCell(4).stringCellValue

        MenuItem(
            name = itemName,
            servingSize = servingSize,
            normalPrice = normalPrice,
            discountedPrice = discountedPrice,
            container = null,
            day = day,
            firstPosibleDay = LocalDate.now(), // TODO remove this field if not needed
            lastPosibleDay = endDate,
            recurringDays = 0
        )
    }

    private fun calculateContainers(
        sheet: Sheet
    ): List<Container> = (30..35).toList().map { rowNumber ->
        val row = sheet.getRow(rowNumber)

        val containerName = row.getCell(1).stringCellValue
        val containerPrice = handleGetPrice(row.getCell(2))

        Container(
            name = containerName,
            price = containerPrice
        )
    }

    @Transactional
    fun saveMenuItems(menuItems: List<MenuItem>) {

        val menuItemsByName = menuItems.groupBy { it.name }.mapValues { keyValuePair ->
            keyValuePair.value.let { menuItems ->
                val firstItem = menuItems.first()
                val recurringDays = menuItems.sumOf { it.computeDay() }
                MenuItem(
                    name = keyValuePair.key,
                    servingSize = firstItem.servingSize,
                    normalPrice = firstItem.normalPrice,
                    discountedPrice = firstItem.discountedPrice,
                    container = null,
                    day = null,
                    firstPosibleDay = firstItem.firstPosibleDay,
                    lastPosibleDay = firstItem.lastPosibleDay,
                    recurringDays = recurringDays
                )
            }
        }

        menuItemRepository.saveAll(menuItemsByName.values.toList())
    }
}