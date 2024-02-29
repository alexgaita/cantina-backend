package com.example.cantinabackend.services

import com.example.cantinabackend.domain.dtos.MissingContainersDto
import com.example.cantinabackend.domain.entities.Container
import com.example.cantinabackend.domain.entities.DailyMenu
import com.example.cantinabackend.domain.entities.MenuItem
import com.example.cantinabackend.domain.enums.WeekDay
import com.example.cantinabackend.domain.repositories.ContainerRepository
import com.example.cantinabackend.domain.repositories.DailyMenuRepository
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
    private val containerRepository: ContainerRepository,
    private val dailyMenuRepository: DailyMenuRepository
) {

    @Transactional
    fun readMenu(workbook: Workbook): MissingContainersDto {

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

            if (day == 0) {
                containerList.addAll(calculateContainers(sheet))
            }
        }

        dailyMenuRepository.deleteByLastDay(menuInterval!!.second)

        val savedItems = saveMenuItems(menuItemsList)

        val dailyMenuWithContainers = findAllContainersForDailyMenu(savedItems, dailyMenuList)

        dailyMenuRepository.saveAll(dailyMenuWithContainers)

        containerRepository.saveAll(containerList.distinctBy { it.name })

        val itemsWithoutContainers = savedItems.filter { it.containers.isEmpty() }
        val containers = containerRepository.findAll()
        val dailyMenusWithoutContainers = dailyMenuWithContainers.filter { it.containers.isEmpty() }

        return MissingContainersDto(containers, itemsWithoutContainers, dailyMenusWithoutContainers)
    }

    private fun findAllContainersForDailyMenu(menuItems: List<MenuItem>, dailyMenus: List<DailyMenu>): List<DailyMenu> {
        val menuItemsByName = menuItems.associateBy { it.name }
        dailyMenus.forEach { dailyMenu ->

            val possibleContainers = mutableListOf<Container>()
            dailyMenu.description.split("/").forEach { menuItemName ->
                menuItemsByName[menuItemName]?.containers.let { containers ->
                    if (containers != null) {
                        possibleContainers.addAll(containers)
                    }
                }
            }
            dailyMenu.containers.addAll(possibleContainers.distinctBy { it.name })
        }
        return dailyMenus
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
    ): List<DailyMenu> {

        val menuColumns = listOf(0, 2)
        val dailyMenuItems = mutableListOf<DailyMenu>()

        for (menuColumn in menuColumns) {

            var menuDescription = ""

            (3..5).toList().forEach { rowNumber ->
                val row = sheet.getRow(rowNumber)

                val description = row.getCell(menuColumn).stringCellValue
                if (description.isEmpty()) return@forEach
                menuDescription += "${description.trimEnd().trimStart()}/"
            }

            dailyMenuItems.add(
                DailyMenu(
                    description = menuDescription,
                    recurringDays = WeekDay.entries[day].value,
                    lastPosibleDay = endDate
                )
            )
        }

        return dailyMenuItems
    }

    private fun calculateMenuItems(
        sheet: Sheet,
        day: Int,
        endDate: LocalDate,
    ): List<MenuItem> {

        var skipNextRow = false

        val menuItems = mutableListOf<MenuItem>()

        (9..28).toList().forEach { rowNumber ->
            val row = sheet.getRow(rowNumber)
            val nextRow = sheet.getRow(rowNumber + 1)

            if (skipNextRow) {
                skipNextRow = false
                return@forEach
            }

            if (rowNumber != 28 && nextRow.getCell(3).numericCellValue == 0.0) {
                skipNextRow = true
                val itemName =
                    (row.getCell(1).stringCellValue + " " + nextRow.getCell(1).stringCellValue).replace(
                        "\\s+".toRegex(),
                        " "
                    )
                val discountedPriceTeacher = handleGetPrice(row.getCell(3))
                val normalPrice = handleGetPrice(row.getCell(4))
                val servingSize = row.getCell(5).stringCellValue

                menuItems.add(
                    MenuItem(
                        name = itemName,
                        servingSize = servingSize,
                        normalPrice = normalPrice,
                        discountedPrice = discountedPriceTeacher,
                        day = day,
                        firstPosibleDay = LocalDate.now(), // TODO remove this field if not needed
                        lastPosibleDay = endDate,
                        recurringDays = 0
                    )
                )
                return@forEach
            }

            val itemName = row.getCell(1).stringCellValue
//       TODO  val discountedPriceStudent = handleGetPrice(row.getCell(2))
            val discountedPriceTeacher = handleGetPrice(row.getCell(3))
            val normalPrice = handleGetPrice(row.getCell(4))
            val servingSize = row.getCell(5).stringCellValue

            menuItems.add(
                MenuItem(
                    name = itemName,
                    servingSize = servingSize,
                    normalPrice = normalPrice,
                    discountedPrice = discountedPriceTeacher,
                    day = day,
                    firstPosibleDay = LocalDate.now(), // TODO remove this field if not needed
                    lastPosibleDay = endDate,
                    recurringDays = 0
                )
            )
        }

        return menuItems
    }

    private fun calculateContainers(
        sheet: Sheet
    ): List<Container> = (31..36).toList().map { rowNumber ->
        val row = sheet.getRow(rowNumber)

        val containerName = row.getCell(1).stringCellValue
        val containerPrice = handleGetPrice(row.getCell(2))

        Container(
            name = containerName,
            price = containerPrice
        )
    }

    @Transactional
    fun saveMenuItems(menuItems: List<MenuItem>): List<MenuItem> {

        val menuItemsByName = menuItems.groupBy { it.name }.mapValues { keyValuePair ->
            keyValuePair.value.let { menuItems ->
                val firstItem = menuItems.first()
                val recurringDays = menuItems.sumOf { it.computeDay() }

                MenuItem(
                    name = keyValuePair.key,
                    servingSize = firstItem.servingSize,
                    normalPrice = firstItem.normalPrice,
                    discountedPrice = firstItem.discountedPrice,
                    containers = menuItemRepository.findContainersByMenuItemName(keyValuePair.key).toMutableList(),
                    day = null,
                    firstPosibleDay = firstItem.firstPosibleDay,
                    lastPosibleDay = firstItem.lastPosibleDay,
                    recurringDays = recurringDays
                )
            }
        }

        menuItemRepository.saveAll(menuItemsByName.values.toList())
        return menuItemsByName.values.toList()
    }
}