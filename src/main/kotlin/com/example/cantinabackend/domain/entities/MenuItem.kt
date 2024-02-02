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

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", referencedColumnName = "name")
    var container: Container? = null,

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