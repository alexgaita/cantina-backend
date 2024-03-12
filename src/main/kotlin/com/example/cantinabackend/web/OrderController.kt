package com.example.cantinabackend.web

import com.example.cantinabackend.domain.dtos.OrderCreateDto
import com.example.cantinabackend.domain.dtos.PaymentIntentDto
import com.example.cantinabackend.services.OrderService
import com.example.cantinabackend.services.PaymentService
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
    private val paymentService: PaymentService
) : IOrderController {

    @PostMapping("/webhook")
    @Transactional
    fun handleWebhook(@RequestBody payload: String, @RequestHeader("Stripe-Signature") sigHeader: String) {
        val productId = paymentService.webHook(payload, sigHeader)
        if (productId.isEmpty()) {
            logger.info { "No Product Id" }
            return
        }
        orderService.confirmCardOrder(productId)
    }

    @PostMapping("/confirm/{orderId}")
    override fun confirmOrder(@PathVariable orderId: Int) = orderService.confirmOrder(orderId)

    @PostMapping()
    override fun placeOrder(order: OrderCreateDto): PaymentIntentDto = orderService.placeOrder(order)

}


