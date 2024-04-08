package com.example.cantinabackend.web

import com.example.cantinabackend.domain.dtos.OrderCreateDto
import com.example.cantinabackend.domain.dtos.OrdersByDateDto
import com.example.cantinabackend.domain.dtos.PaymentIntentDto
import com.example.cantinabackend.domain.entities.OrderStatus
import com.example.cantinabackend.services.OrderService
import com.example.cantinabackend.services.PaymentService
import com.example.cantinabackend.services.UserService
import com.example.cantinabackend.web.swagger.IOrderController
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/order")
@Validated
class OrderController(
    private val orderService: OrderService,
    private val paymentService: PaymentService,
    private val userService: UserService
) : IOrderController {

    @PostMapping("/webhook")
    @Transactional
    fun handleWebhook(@RequestBody payload: String, @RequestHeader("Stripe-Signature") sigHeader: String) {
        val (productId, cartelaPayment) = paymentService.webHook(payload, sigHeader)

        if (productId.isEmpty()) {
            logger.info { "No Product Id" }
            return
        }

        if (cartelaPayment != null) {
            userService.confirmDiscountOrder(cartelaPayment)
        } else {
            orderService.confirmCardOrder(productId)
        }
    }

    @PostMapping("/confirmDetails/{orderId}")
    override fun confirmOrderDetails(
        @PathVariable orderId: Int,
        @RequestParam address: Int,
        @RequestParam phoneNumber: String
    ) =
        orderService.confirmOrderDetails(orderId, address, phoneNumber)

    @PostMapping()
    override fun placeOrder(order: OrderCreateDto): PaymentIntentDto = orderService.placeOrder(order)

    @GetMapping("/user")
    override fun getAllOrdersForUser(): OrdersByDateDto = orderService.getAllOrdersForUser()

    @GetMapping("/all")
    override fun getAllOrders(): OrdersByDateDto = orderService.getAllOrders()

    @DeleteMapping("/{orderId}")
    override fun deleteOrder(@PathVariable orderId: Int) = orderService.deleteOrder(orderId)

    @PutMapping("/changeStatus/{orderId}")
    override fun changeOrderStatus(@PathVariable orderId: Int, @RequestParam status: String) {
        orderService.changeOrderStatus(orderId, OrderStatus.valueOf(status))
    }

}


