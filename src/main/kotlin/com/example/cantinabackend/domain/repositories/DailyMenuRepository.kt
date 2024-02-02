package com.example.cantinabackend.domain.repositories

import com.example.cantinabackend.domain.entities.DailyMenu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DailyMenuRepository : JpaRepository<DailyMenu, Int> {

}