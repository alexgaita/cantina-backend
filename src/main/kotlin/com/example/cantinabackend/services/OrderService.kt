package com.example.cantinabackend.services

import com.example.cantinabackend.config.annotations.Permission
import com.example.cantinabackend.config.annotations.RequiredPermissions
import com.example.cantinabackend.domain.dtos.OrderCreateDto
import com.example.cantinabackend.domain.dtos.PaymentIntentDto
import com.example.cantinabackend.domain.entities.Order
import com.example.cantinabackend.domain.entities.OrderItem
import com.example.cantinabackend.domain.entities.OrderStatus
import com.example.cantinabackend.domain.repositories.MenuItemRepository
import com.example.cantinabackend.domain.repositories.OrderItemRepository
import com.example.cantinabackend.domain.repositories.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val paymentService: PaymentService,
    private val userService: UserService,
    private val menuItemRepository: MenuItemRepository,
    private val orderItemRepository: OrderItemRepository,
    private val orderRepository: OrderRepository
) {

    @Transactional
    @RequiredPermissions([Permission.NORMAL_USER])
    fun placeOrder(order: OrderCreateDto): PaymentIntentDto {

        val user = userService.findUser()

        if (user.addresses.isEmpty()) {
            throw IllegalStateException("User has no address")
        }
        val address = user.addresses.first { it.isCurrent }
        if (address.value.isBlank()) {
            throw IllegalStateException("User has no address")
        }

        val menuItems = menuItemRepository.findAllById(order.items.map { it.productId }).associateBy { it.name }

        if (menuItems.keys.size != order.items.size) {
            throw IllegalStateException("Some items are not found")
        }

        val hasDiscount = order.discountIds.isNotEmpty()

        var amount = 0.0

        val orderEntity = Order(
            description = order.comment,
            user = user,
            address = address,
            useSilverware = order.wantSilverware
        )

        val orderItems = order.items.map {
            val menuItem = menuItems[it.productId] ?: throw IllegalStateException("Item not found")
            val price = if (hasDiscount) menuItem.discountedPrice else menuItem.normalPrice
            val containerPrice = menuItem.containers.sumOf { container -> container.price }
            amount += price * it.quantity + containerPrice

            OrderItem(price, it.quantity, menuItem, orderEntity)
        }


        orderEntity.totalPrice = amount + 10 // delivery fee
        orderEntity.orderItems = orderItems.toMutableList()

        var paymentSecret: String? = null

        if (order.byCard) {
            orderEntity.status = OrderStatus.PAYMENT_REQUIRED

            val payment = paymentService.createPaymentIntent(orderEntity.totalPrice)
            paymentSecret = payment.clientSecret
            orderEntity.paymentId = payment.id
        }

        val savedOrder = orderRepository.save(orderEntity)

        return PaymentIntentDto(savedOrder.id, paymentSecret)
    }

    @Transactional
    @RequiredPermissions([Permission.NORMAL_USER])
    fun confirmOrder(orderId: Int) {
        val order = orderRepository.findById(orderId).orElseThrow { IllegalStateException("Order not found") }

        if (order.status == OrderStatus.PAYMENT_REQUIRED) {
            throw IllegalStateException("Order is in payment required state")
        }

        order.status = OrderStatus.IN_PROGRESS
    }

    @Transactional
    fun confirmCardOrder(paymentId: String?) {
        if (paymentId == null) {
            throw IllegalStateException("Payment id is null")
        }
        val order = orderRepository.findByPaymentId(paymentId) ?: throw IllegalStateException("Order not found")

        order.status = OrderStatus.IN_PROGRESS
    }
}