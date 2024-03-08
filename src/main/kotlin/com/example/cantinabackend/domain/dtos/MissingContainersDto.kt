package com.example.cantinabackend.domain.dtos

import com.example.cantinabackend.domain.entities.Container
import com.example.cantinabackend.domain.entities.MenuItem

data class MissingContainersDto(
    val containers: List<Container>,
    val items: List<MenuItem>,
)