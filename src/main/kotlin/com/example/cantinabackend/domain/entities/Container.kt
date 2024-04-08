package com.example.cantinabackend.domain.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Container(
    @Id
    val name: String,

    @Column(columnDefinition = "DECIMAL(4,2)")
    val price: Double,
)
