package com.example.cantinabackend.services

import com.example.cantinabackend.domain.entities.Address
import com.example.cantinabackend.domain.entities.User
import com.example.cantinabackend.domain.repositories.AddressRepository
import com.example.cantinabackend.domain.repositories.CartelaRepository
import com.example.cantinabackend.domain.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.util.*

class UserServiceTest {

    private val securityAuthenticationService: SecurityAuthenticationService = mockk()
    private val userRepository: UserRepository = mockk()
    private val addressRepository: AddressRepository = mockk()
    private val cartelaRepository: CartelaRepository = mockk()
    private val paymentService: PaymentService = mockk()

    private val userService = UserService(
        securityAuthenticationService,
        userRepository,
        addressRepository,
        cartelaRepository,
        paymentService
    )

    private fun createUser(): User {
        val userId = UUID.randomUUID()
        val name = "name"
        val email = "email"
        return User(userId, null, name, email)
    }

    @Test
    fun `when getting user and everything is ok then return user`() {
        //ARRANGE
        val user = createUser()
        every { securityAuthenticationService.getUserId() } returns user.id
        every { userRepository.findByIdOrNull(user.id) } returns user

        //ACT
        val actual = userService.findUser()

        //ASSERT
        assertEquals(user, actual)
    }

    @Test
    fun `when getting user and it doesn't exist then throw EntityNotFoundException`() {
        //ARRANGE
        every { securityAuthenticationService.getUserId() } returns UUID.randomUUID()
        every { userRepository.findByIdOrNull(any()) } returns null

        //ASSERT & ACT
        val error = assertThrows<EntityNotFoundException> {
            userService.findUser()
        }

        assertEquals("User not found", error.message)
    }

    @Test
    fun `when deleting user address and it doesn't exist then throw EntityNotFoundException`() {
        //ARRANGE
        val userId = UUID.randomUUID()
        val addressId = 1
        every { userRepository.findByIdOrNull(userId) } returns createUser()
        every { securityAuthenticationService.getUserId() } returns userId
        every { addressRepository.findByIdOrNull(addressId) } returns null

        //ASSERT & ACT
        val error = assertThrows<EntityNotFoundException> {
            userService.deleteUserAddress(addressId)
        }

        assertEquals("Address not found", error.message)
    }

    @Test
    fun `when deleting user address and everything is ok then delete address`() {
        //ARRANGE
        val user = createUser()
        val adressId = 1
        val address = Address("adress", true, user)
        every { securityAuthenticationService.getUserId() } returns user.id
        every { addressRepository.findByIdOrNull(any()) } returns address
        every { userRepository.findByIdOrNull(any()) } returns user
        every { addressRepository.delete(any()) } returns Unit

        //ACT
        userService.deleteUserAddress(adressId)

        //ASSERT
        verify(exactly = 1) { addressRepository.delete(address) }
    }

}