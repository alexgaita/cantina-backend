package com.example.cantinabackend.domain.dtos

import com.example.cantinabackend.domain.entities.DailyMenu
import com.example.cantinabackend.domain.entities.MenuItem

data class MenuDto(
    val menu: List<DailyMenu>,
    val normalItems: List<MenuItem>
)