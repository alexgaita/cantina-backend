package com.example.cantinabackend.services

import com.example.cantinabackend.config.annotations.Permission
import com.example.cantinabackend.config.annotations.RequiredPermissions
import com.example.cantinabackend.domain.dtos.*
import com.example.cantinabackend.domain.entities.Order
import com.example.cantinabackend.domain.entities.OrderItem
import com.example.cantinabackend.domain.entities.OrderStatus
import com.example.cantinabackend.domain.repositories.CartelaRepository
import com.example.cantinabackend.domain.repositories.MenuItemRepository
import com.example.cantinabackend.domain.repositories.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class OrderService(
    private val paymentService: PaymentService,
    private val userService: UserService,
    private val menuItemRepository: MenuItemRepository,
    private val orderRepository: OrderRepository,
    private val cartelaRepository: CartelaRepository,
    private val emailService: EmailService
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

        val cartele = cartelaRepository.findAllByIdsAndUser(order.discountIds.map { UUID.fromString(it) }, user.id)

        if (cartele.size != order.discountIds.size) {
            throw IllegalStateException("Some cartele are not found")
        }

        if (cartele.any { !it.isActive }) {
            throw IllegalStateException("Some cartele are not active")
        }

        val discountAmount = cartele.sumOf { it.value }

        cartele.forEach { it.isActive = false }

        var amount = 0.0

        val orderEntity = Order(
            description = order.comment,
            user = user,
            address = address,
            useSilverware = order.wantSilverware,
            phoneNumber = user.phoneNumber ?: throw IllegalStateException("User has no phone number")
        )

        val orderItems = order.items.map {
            val menuItem = menuItems[it.productId] ?: throw IllegalStateException("Item not found")
            val price = menuItem.normalPrice
            val containerPrice = menuItem.containers.sumOf { container -> container.price * it.quantity }
            amount += price * it.quantity + containerPrice

            OrderItem(price, it.quantity, menuItem, orderEntity)
        }

        val deliveryAmount = 10

        orderEntity.totalPrice = amount + deliveryAmount - discountAmount
        orderEntity.orderItems = orderItems.toMutableList()

        var paymentSecret: String? = null

        if (order.byCard) {
            orderEntity.status = OrderStatus.PAYMENT_REQUIRED

            val payment = paymentService.createPaymentIntent(orderEntity.totalPrice)
            paymentSecret = payment.clientSecret
            orderEntity.paymentId = payment.id
        }

        val savedOrder = orderRepository.save(orderEntity)

        cartele.forEach { it.order = savedOrder }

        if (orderEntity.status == OrderStatus.CREATED) {
            emailService.sendMail(savedOrder)
        }

        return PaymentIntentDto(savedOrder.id, paymentSecret)
    }

    @Transactional
    @RequiredPermissions([Permission.NORMAL_USER])
    fun confirmOrderDetails(orderId: Int, address: Int, phoneNumber: String) {
        val order = orderRepository.findById(orderId).orElseThrow { IllegalStateException("Order not found") }

        val actualAddress =
            order.user.addresses.find { it.id == address } ?: throw IllegalStateException("Address not found")

        order.address = actualAddress
        order.phoneNumber = phoneNumber
    }

    @Transactional
    fun confirmCardOrder(paymentId: String?) {
        if (paymentId == null) {
            throw IllegalStateException("Payment id is null")
        }
        val order = orderRepository.findByPaymentId(paymentId) ?: throw IllegalStateException("Order not found")

        order.status = OrderStatus.CREATED

        emailService.sendMail(order)
    }

    @Transactional
    @RequiredPermissions([Permission.NORMAL_USER])
    fun getAllOrdersForUser(): OrdersByDateDto {
        val user = userService.findUser()

        val orders = orderRepository.findAllOrdersByUserId(user.id)

        val ordersMappedByMonth = orders.groupBy { order ->
            LocalDate.ofInstant(order.createdAt, ZoneId.systemDefault()).let {
                LocalDate.of(it.year, it.month, 1)
            }
        }.mapValues { (_, orders) ->
            orders.map { order ->
                OrderViewDto(
                    order.id,
                    order.orderItems.map { orderItem ->
                        OrderItemViewDto(
                            orderItem.menuItem.name,
                            orderItem.quantity,
                            orderItem.price
                        )
                    },
                    getContainersList(order),
                    order.description,
                    order.cartele.map { it.id.toString() },
                    order.address.value,
                    order.phoneNumber,
                    order.status.name,
                    LocalDateTime.ofInstant(order.createdAt, ZoneId.systemDefault()),
                    order.totalPrice,
                    order.paymentId == null
                )
            }.sortedByDescending { it.createdAt }
        }

        return OrdersByDateDto(ordersMappedByMonth)

    }

    @Transactional
    @RequiredPermissions([Permission.ADMIN])
    fun getAllOrders(): OrdersByDateDto {

        val orders = orderRepository.findAll()

        val ordersMappedByDay = orders.groupBy { order ->
            LocalDate.ofInstant(order.createdAt, ZoneId.systemDefault())
        }.mapValues { (_, orders) ->
            orders.map { order ->
                OrderViewDto(
                    order.id,
                    order.orderItems.map { orderItem ->
                        OrderItemViewDto(
                            orderItem.menuItem.name,
                            orderItem.quantity,
                            orderItem.price
                        )
                    },
                    getContainersList(order),
                    order.description,
                    order.cartele.map { it.id.toString() },
                    order.address.value,
                    order.user.phoneNumber,
                    order.status.name,
                    LocalDateTime.ofInstant(order.createdAt, ZoneId.systemDefault()),
                    order.totalPrice,
                    order.paymentId == null
                )
            }.sortedByDescending { it.createdAt }
        }

        return OrdersByDateDto(ordersMappedByDay)
    }

    @Transactional
    @RequiredPermissions([Permission.ADMIN])
    fun deleteOrder(orderId: Int) {
        orderRepository.deleteById(orderId)
    }

    @Transactional
    @RequiredPermissions([Permission.ADMIN])
    fun changeOrderStatus(orderId: Int, status: OrderStatus) {
        val order = orderRepository.findById(orderId).orElseThrow { IllegalStateException("Order not found") }

        when (status) {
            OrderStatus.CREATED -> throw IllegalStateException("Cannot change status to created")
            OrderStatus.PAYMENT_REQUIRED -> throw IllegalStateException("Cannot change status to payment required")
            else -> {
                order.status = status
            }
        }
    }

    fun getContainersList(order: Order): List<ContainerOrderDto> {
        val containersMap = mutableMapOf<String, ContainerOrderDto>()

        order.orderItems.forEach {
            it.menuItem.containers.forEach { container ->

                val item = containersMap[container.name]
                if (item != null) {
                    containersMap[container.name] = item.copy(quantity = item.quantity + it.quantity)
                } else {
                    containersMap[container.name] =
                        ContainerOrderDto(container.name, it.quantity)

                }
            }
        }
        return containersMap.values.toList()
    }
}