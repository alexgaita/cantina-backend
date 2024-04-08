package com.example.cantinabackend.services

import com.example.cantinabackend.domain.entities.Order
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.time.LocalDate
import java.time.ZoneId

data class Item(val description: String, val quantity: Int, val price: Double, val total: Double)
data class Invoice(
    val number: String,
    val date: LocalDate,
    val items: List<Item>,
    val total: Double,
    val discount: Double = 0.0
)

data class Customer(val name: String, val address: String)

@Service
class EmailService(private val mailSender: JavaMailSender, private val templateEngine: TemplateEngine) {

    fun sendMail(order: Order) {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage)

        mimeMessageHelper.setFrom("alexandru.gaita@student.upt.ro")
        mimeMessageHelper.setTo(order.user.email)  // order.user.email is not defined in the provided code
        mimeMessageHelper.setSubject("Factura Cantina UPT-Comanda ${order.id}")

        val context = Context()

        val customer = Customer(order.user.name, order.address.value)

        val containersMap = mutableMapOf<String, Item>()

        order.orderItems.forEach {
            it.menuItem.containers.forEach { container ->

                val item = containersMap[container.name]
                if (item != null) {
                    containersMap[container.name] = item.copy(quantity = item.quantity + it.quantity)
                } else {
                    containersMap[container.name] =
                        Item(container.name, it.quantity, container.price, container.price * it.quantity)

                }
            }
        }
        val containersList = containersMap.values.toList()

        val invoice = Invoice(
            number = order.id.toString(),
            date = LocalDate.ofInstant(order.createdAt, ZoneId.systemDefault()),
            items = order.orderItems.map { Item(it.menuItem.name, it.quantity, it.price, it.price * it.quantity) }
                .plus(containersList),
            total = order.totalPrice,
            discount = order.cartele.sumOf { it.value }
        )

        context.setVariable("invoice", invoice)
        context.setVariable("customer", customer)

        val processedString = templateEngine.process("template", context)

        mimeMessageHelper.setText(processedString, true)

        mailSender.send(mimeMessage)
    }
}