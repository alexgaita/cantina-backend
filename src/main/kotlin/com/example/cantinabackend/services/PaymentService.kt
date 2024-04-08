package com.example.cantinabackend.services

import com.example.cantinabackend.config.annotations.Permission
import com.example.cantinabackend.config.annotations.RequiredPermissions
import com.example.cantinabackend.domain.dtos.CartelaPaymentDto
import com.stripe.Stripe
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.model.PaymentIntent
import com.stripe.net.Webhook
import com.stripe.param.PaymentIntentCreateParams
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class PaymentService {

    @Value("\${stripe.api.key}")
    lateinit var STRIPE_API_KEY: String

    @Value("\${stripe.api.webhook.secret}")
    lateinit var WEBHOOK_SECRET: String

    @RequiredPermissions([Permission.NORMAL_USER])
    @Transactional
    fun createPaymentIntent(amount: Double, metadata: Map<String, String> = mapOf()): PaymentIntent {

        Stripe.apiKey = STRIPE_API_KEY

        val paymentIntentParams = PaymentIntentCreateParams.builder()
            .setAmount((amount * 100).toLong())
            .setCurrency("RON")
            .addPaymentMethodType("card")
            .setReceiptEmail("gaita@mailinator.com")
            .putAllMetadata(metadata)

            .build()
        val paymentIntent = PaymentIntent.create(paymentIntentParams)
        return paymentIntent
    }

    @Transactional
    fun webHook(payload: String, sigHeader: String): Pair<String, CartelaPaymentDto?> {

        var event: Event? = null
        try {
            event = Webhook.constructEvent(
                payload,
                sigHeader,
                WEBHOOK_SECRET
            )
        } catch (e: SignatureVerificationException) {
            // Invalid signature
            return Pair("", null)
        }
        // Handle the event
        when (event.type) {

            "payment_intent.succeeded" -> {
                val paymentIntent = event.data
                val paymentIntentData = paymentIntent.`object` as PaymentIntent

                val metadata = paymentIntentData.metadata

                var cartelaPaymentDto: CartelaPaymentDto? = null

                if (metadata.isNotEmpty()) {
                    cartelaPaymentDto = CartelaPaymentDto(
                        metadata["amount"]!!.toInt(),
                        metadata["quantity"]!!.toInt(),
                        UUID.fromString(metadata["userId"]!!)
                    )
                    logger.info { "Cartela Payment: $cartelaPaymentDto" }
                }

                return Pair(paymentIntentData.id, cartelaPaymentDto)
            }

            "payment_intent.created" -> {
                logger.info { "Payment intent created" }
            }

            else -> {

                logger.info { "Unhandled event type: " + event.type }
            }
        }
        return Pair("", null)
    }

}