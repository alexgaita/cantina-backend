package com.example.cantinabackend.domain.repositories

import com.example.cantinabackend.domain.entities.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderRepository : JpaRepository<Order, Int> {

    fun findByPaymentId(paymentId: String): Order?

    @Query(
        """
        SELECT o
        FROM Order o
        WHERE o.user.id = :userId
    """
    )
    fun findAllOrdersByUserId(userId: UUID): List<Order>

}