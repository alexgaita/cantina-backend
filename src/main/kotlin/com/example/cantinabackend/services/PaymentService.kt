package com.example.cantinabackend.services

import com.example.cantinabackend.config.annotations.Permission
import com.example.cantinabackend.config.annotations.RequiredPermissions
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

private val logger = KotlinLogging.logger {}

@Service
class PaymentService {

    @Value("\${stripe.api.key}")
    lateinit var STRIPE_API_KEY: String

    @Value("\${stripe.api.webhook.secret}")
    lateinit var WEBHOOK_SECRET: String

    @RequiredPermissions([Permission.NORMAL_USER])
    @Transactional
    fun createPaymentIntent(amount: Double): PaymentIntent {

        Stripe.apiKey = STRIPE_API_KEY
        val paymentIntentParams = PaymentIntentCreateParams.builder()
            .setAmount((amount * 100).toLong())
            .setCurrency("RON")
            .addPaymentMethodType("card")
            .build()
        val paymentIntent = PaymentIntent.create(paymentIntentParams)
        return paymentIntent
    }

    @Transactional
    fun webHook(payload: String, sigHeader: String): String {

        var event: Event? = null
        try {
            event = Webhook.constructEvent(
                payload,
                sigHeader,
                WEBHOOK_SECRET
            )
        } catch (e: SignatureVerificationException) {
            // Invalid signature
            return ""
        }
        // Handle the event
        when (event.type) {

            "payment_intent.succeeded" -> {
                val paymentIntent = event.data
                val paymentIntentId = paymentIntent.`object` as PaymentIntent

                return paymentIntentId.id
                logger.info { "It worked" }
            }

            "payment_intent.created" -> {
                logger.info { "Payment intent created" }
            }

            else -> {

                logger.info { "Unhandled event type: " + event.type }
            }
        }
        return ""
    }

}