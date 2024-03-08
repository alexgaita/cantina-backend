package com.example.cantinabackend.domain.enums

enum class WeekDay(val value: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(4),
    THURSDAY(8),
    FRIDAY(16);

    companion object {
        infix fun from(value: Int): WeekDay? = WeekDay.entries.firstOrNull { it.value == value }
    }
}