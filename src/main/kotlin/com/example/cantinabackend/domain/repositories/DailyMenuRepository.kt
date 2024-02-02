package com.example.cantinabackend.domain.repositories

import com.example.cantinabackend.domain.entities.DailyMenu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyMenuRepository : JpaRepository<DailyMenu, Int> {

    @Modifying
    @Query("""DELETE FROM DailyMenu d WHERE d.lastPosibleDay <= :lastDay""")
    fun deleteByLastDay(lastDay: LocalDate)

    @Query(
        "SELECT * FROM daily_menu  WHERE recurring_days = :day AND last_posible_day >= :currentDate",
        nativeQuery = true
    )
    fun findMenusByDay(day: Int, currentDate: LocalDate): List<DailyMenu>
}