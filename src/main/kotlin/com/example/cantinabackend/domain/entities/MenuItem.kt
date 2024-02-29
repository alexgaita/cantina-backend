package com.example.cantinabackend.domain.entities

import com.example.cantinabackend.domain.enums.WeekDay
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class MenuItem(
    @Id
    val name: String,

    @Column
    var servingSize: String,

    @Column(columnDefinition = "DECIMAL(4,2)")
    var normalPrice: Double,

    @Column(columnDefinition = "DECIMAL(4,2)")
    var discountedPrice: Double,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "menu_item_containers",
        joinColumns = [JoinColumn(name = "menu_item_id", referencedColumnName = "name")],
        inverseJoinColumns = [JoinColumn(name = "container_id", referencedColumnName = "name")]
    )
    val containers: MutableList<Container> = mutableListOf(),

    @Column
    var recurringDays: Int,

    @Column
    var firstPosibleDay: LocalDate,

    @Column
    var lastPosibleDay: LocalDate,

    @Transient
    val day: Int? = null
) {

    fun computeDay(): Int = when (day) {
        0 -> WeekDay.MONDAY.value
        1 -> WeekDay.TUESDAY.value
        2 -> WeekDay.WEDNESDAY.value
        3 -> WeekDay.THURSDAY.value
        4 -> WeekDay.FRIDAY.value
        else -> throw Exception("Invalid day")
    }

}