package com.example.cantinabackend.domain.repositories

import com.example.cantinabackend.domain.entities.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Int> {

}