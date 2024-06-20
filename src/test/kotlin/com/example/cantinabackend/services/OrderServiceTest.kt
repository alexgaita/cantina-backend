package com.example.cantinabackend.services

import com.example.cantinabackend.domain.dtos.OrderCreateDto
import com.example.cantinabackend.domain.dtos.OrderItemCreateDto
import com.example.cantinabackend.domain.entities.*
import com.example.cantinabackend.domain.repositories.CartelaRepository
import com.example.cantinabackend.domain.repositories.MenuItemRepository
import com.example.cantinabackend.domain.repositories.OrderRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.time.Instant
import java.time.LocalDate
import java.util.*

class OrderServiceTest {

    private val paymentService: PaymentService = mockk()
    private val userService: UserService = mockk()
    private val menuItemRepository: MenuItemRepository = mockk()
    private val orderRepository: OrderRepository = mockk()
    private val cartelaRepository: CartelaRepository = mockk()
    private val emailService: EmailService = mockk()

    private val orderService = OrderService(
        paymentService,
        userService,
        menuItemRepository,
        orderRepository,
        cartelaRepository,
        emailService
    )

    private fun createUser(addresses: Set<Address> = setOf()) = User(
        UUID.randomUUID(),
        null,
        "name",
        "email",
        addresses = addresses.toMutableSet()
    )

    private fun createAdress(user: User) = Address(
        value = "address",
        isCurrent = true,
        user = user
    )

    private fun createOrder(
        user: User = createUser(),
        address: Address = createAdress(user),
        createdAt: Instant = Instant.now()
    ) = Order(
        totalPrice = 0.0,
        description = null,
        user = user,
        address = address,
        phoneNumber = "phone",
        createdAt = createdAt,
    )

    private fun createOrderDto(
        items: List<OrderItemCreateDto> = listOf(
            OrderItemCreateDto(
                productId = UUID.randomUUID().toString(),
                quantity = 1

            )
        )
    ) = OrderCreateDto(
        comment = "comment",
        items = items,
        discountIds = listOf(UUID.randomUUID().toString()),
        wantSilverware = false,
        byCard = false,
    )

    @Test
    fun `when getting all orders for user then return all orders for user mapped by month`() {
        //ARRANGE
        val user = createUser()
        val orders = listOf(
            createOrder(user, createdAt = Instant.parse("2024-01-01T00:00:00Z")),
            createOrder(user, createdAt = Instant.parse("2024-01-02T00:00:00Z")),
            createOrder(user, createdAt = Instant.parse("2024-02-01T00:00:00Z")),
        )

        every { userService.findUser() } returns user
        every { orderRepository.findAllOrdersByUserId(any()) } returns orders

        val expectedKeys = listOf(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 1)
        )

        //ACT
        val actual = orderService.getAllOrdersForUser()

        //ASSERT
        assertEquals(expectedKeys.toSet(), actual.items.keys)
        assertEquals(2, actual.items[expectedKeys[0]]?.size)
        assertEquals(1, actual.items[expectedKeys[1]]?.size)
    }

    @Test
    fun `when getting all orders then return all of them mapped by day`() {

        //ARRANGE
        val user = createUser()
        val orders = listOf(
            createOrder(user, createdAt = Instant.parse("2024-01-01T00:00:00Z")),
            createOrder(user, createdAt = Instant.parse("2024-01-01T00:00:00Z")),
            createOrder(user, createdAt = Instant.parse("2024-01-02T00:00:00Z")),
            createOrder(user, createdAt = Instant.parse("2024-01-03T00:00:00Z")),
        )

        every { userService.findUser() } returns user
        every { orderRepository.findAll() } returns orders

        val expectedKeys = listOf(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 2),
            LocalDate.of(2024, 1, 3)
        )

        //ACT
        val actual = orderService.getAllOrders()

        //ASSERT
        assertEquals(expectedKeys.toSet(), actual.items.keys)
        assertEquals(2, actual.items[expectedKeys[0]]?.size)
        assertEquals(1, actual.items[expectedKeys[1]]?.size)
        assertEquals(1, actual.items[expectedKeys[2]]?.size)
    }

    @Test
    fun `when confirming order details for normal order and order doesn't exist then throw IllegalStateException`() {

        //ARRANGE
        val orderId = 1
        every { orderRepository.findByIdOrNull(orderId) } returns null

        //ACT & ASSERT
        val error = assertThrows<IllegalStateException> {
            orderService.confirmOrderDetails(orderId, 1, "")
        }

        assertEquals("Order not found", error.message)
    }

    @Test
    fun `when confirming card order and payment id is null then throw IllegalArgumentException`() {

        //ACT & ASSERT
        val error = assertThrows<IllegalStateException> {
            orderService.confirmCardOrder(null)
        }

        assertEquals("Payment id is null", error.message)
    }

    @Test
    fun `when confirming card order and order is not found then throw IllegalStateException`() {

        //ARRANGE
        val paymentId = "paymentId"
        every { orderRepository.findByPaymentId(paymentId) } returns null

        //ACT & ASSERT
        val error = assertThrows<IllegalStateException> {
            orderService.confirmCardOrder(paymentId)
        }

        assertEquals("Order not found", error.message)
    }

    @Test
    fun `when confirming card order then set order status to CREATED and send email`() {

        //ARRANGE
        val paymentId = "paymentId"
        val order = createOrder().let { it.status = OrderStatus.PAYMENT_REQUIRED; it }

        val orderParameter = slot<Order>()
        every { orderRepository.findByPaymentId(paymentId) } returns order
        every { emailService.sendMail(capture(orderParameter)) } returns Unit

        //ACT
        orderService.confirmCardOrder(paymentId)

        //ASSERT
        assertEquals(OrderStatus.CREATED, orderParameter.captured.status)
    }

    @Test
    fun `when placing order and user has no address then throw IllegalStateException`() {

        //ARRANGE
        val user = createUser()
        val order = createOrderDto()
        every { userService.findUser() } returns user

        //ACT & ASSERT
        val error = assertThrows<IllegalStateException> {
            orderService.placeOrder(order)
        }

        assertEquals("User has no address", error.message)
    }

    @Test
    fun `when placing order and items size doesn't match database then throw IllegalStateException`() {

        //ARRANGE
        val user = createUser()
        val address = createAdress(user)
        user.addresses.add(address)

        val orderDto = createOrderDto()

        every { userService.findUser() } returns user
        every { menuItemRepository.findAllById(any()) } returns emptyList()

        //ACT & ASSERT

        val error = assertThrows<IllegalStateException> {
            orderService.placeOrder(orderDto)
        }

        assertEquals("Some items are not found", error.message)
    }

    @Test
    fun `when placing order and some discounts are not found in database then throw IllegalStateException`() {

        //ARRANGE
        val user = createUser()
        val address = createAdress(user)

        user.addresses.add(address)

        val orderDto = createOrderDto(listOf())

        every { userService.findUser() } returns user
        every { menuItemRepository.findAllById(any()) } returns emptyList()
        every { cartelaRepository.findAllByIdsAndUser(any(), any()) } returns emptyList()

        //ACT & ASSERT

        val error = assertThrows<IllegalStateException> {
            orderService.placeOrder(orderDto)
        }

        assertEquals("Some cartele are not found", error.message)
    }

    @Test
    fun `when placing order and some discounts are not active then throw IllegalStateException`() {

        //ARRANGE
        val user = createUser()
        val address = createAdress(user)

        user.addresses.add(address)

        val orderDto = createOrderDto(listOf())
        val discount = Cartela(
            UUID.randomUUID(),
            1.0,
            false,
            user,
            null
        )

        every { userService.findUser() } returns user
        every { menuItemRepository.findAllById(any()) } returns emptyList()
        every { cartelaRepository.findAllByIdsAndUser(any(), any()) } returns listOf(discount)

        //ACT & ASSERT

        val error = assertThrows<IllegalStateException> {
            orderService.placeOrder(orderDto)
        }

        assertEquals("Some cartele are not active", error.message)
    }

}