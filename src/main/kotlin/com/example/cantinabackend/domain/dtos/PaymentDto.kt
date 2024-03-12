package com.example.cantinabackend.domain.dtos

data class PaymentIntentDto(
    val orderId: Int,
    val clientSecret: String?
)

