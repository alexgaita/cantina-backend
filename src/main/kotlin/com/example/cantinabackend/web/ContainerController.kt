package com.example.cantinabackend.web

import com.example.cantinabackend.config.annotations.Permission
import com.example.cantinabackend.config.annotations.RequiredPermissions
import com.example.cantinabackend.domain.repositories.ContainerRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/containers")
@Validated
class ContainerController(
    private val containerRepository: ContainerRepository
) {

    @GetMapping()
    @RequiredPermissions([Permission.ADMIN])
    @Transactional
    fun getContainers(): List<String> = containerRepository.findAll().map { it.name }

}