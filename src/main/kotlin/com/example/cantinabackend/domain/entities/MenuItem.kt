package com.example.cantinabackend.domain.entities

import com.example.cantinabackend.domain.enums.WeekDay
import jakarta.persistence.*
import java.time.LocalDate

enum class ItemType {
    DAILY_MENU,
    SOUP,
    MAIN_COURSE,
    GARNISH,
    DESSERT,
    EXTRA
}

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

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(
        name = "menu_item_containers",
        joinColumns = [JoinColumn(name = "menu_item_id", referencedColumnName = "name")],
        inverseJoinColumns = [JoinColumn(name = "container_id", referencedColumnName = "name")]
    )
    var containers: MutableList<Container> = mutableListOf(),

    @Column
    var recurringDays: Int = 0,

    @Column
    var firstPossibleDay: LocalDate,

    @Column
    var lastPossibleDay: LocalDate,

    @Column
    var photoUrl: String? = null,

    @Column
    @Enumerated(EnumType.STRING)
    var type: ItemType? = null,

    @Transient
    val day: Int? = null,

    ) {

    fun computeRecurringDays(): List<WeekDay> {
        val recurringDaysList = mutableListOf<Int>()
        var temp = recurringDays
        while (temp > 0) {
            val day = temp and -temp
            recurringDaysList.add(day)
            temp = temp and (temp - 1)
        }
        return recurringDaysList.mapNotNull { WeekDay.from(it) }

    }

    fun computeDay(): Int = when (day) {
        0 -> WeekDay.MONDAY.value
        1 -> WeekDay.TUESDAY.value
        2 -> WeekDay.WEDNESDAY.value
        3 -> WeekDay.THURSDAY.value
        4 -> WeekDay.FRIDAY.value
        else -> throw Exception("Invalid day")
    }

}