package com.example.cantinabackend.domain.repositories

import com.example.cantinabackend.domain.entities.Container
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContainerRepository : JpaRepository<Container, String> {

}