package com.example.cantinabackend.domain.repositories

import com.example.cantinabackend.domain.entities.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Int> {

    fun findByPaymentId(paymentId: String): Order?
}