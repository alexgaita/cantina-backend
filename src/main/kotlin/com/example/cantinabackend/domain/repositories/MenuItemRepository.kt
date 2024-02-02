package com.example.cantinabackend.domain.repositories

import com.example.cantinabackend.domain.entities.MenuItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface MenuItemRepository : JpaRepository<MenuItem, String> {

    @Query(
        "SELECT * FROM menu_item  WHERE (recurring_days & :day) > 0 AND last_posible_day >= :currentDate",
        nativeQuery = true
    )
    fun findItemsForDay(day: Int, currentDate: LocalDate): List<MenuItem>

}