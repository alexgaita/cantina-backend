package com.example.cantinabackend.domain.entities

import jakarta.persistence.*
import java.time.LocalDate

@Entity
class DailyMenu(

    @Column
    val description: String,

    @Column
    val recurringDays: Int,

    @Column
    val lastPosibleDay: LocalDate,

    @ManyToMany
    @JoinTable(
        name = "daily_menu_containers",
        joinColumns = [JoinColumn(name = "daily_menu_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "container_id", referencedColumnName = "name")]
    )
    val containers: MutableList<Container> = mutableListOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0
}
