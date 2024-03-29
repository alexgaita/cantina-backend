package com.example.cantinabackend.domain.repositories

import com.example.cantinabackend.domain.entities.Container
import com.example.cantinabackend.domain.entities.MenuItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface MenuItemRepository : JpaRepository<MenuItem, String> {

    @Query(
        "SELECT * FROM menu_item  WHERE (recurring_days & :day) > 0 AND last_possible_day >= :currentDate",
        nativeQuery = true
    )
    fun findItemsForDay(day: Int, currentDate: LocalDate): List<MenuItem>

    @Query(
        """SELECT m.containers FROM MenuItem m WHERE m.name = :name"""
    )
    fun findContainersByMenuItemName(name: String): List<Container>

}