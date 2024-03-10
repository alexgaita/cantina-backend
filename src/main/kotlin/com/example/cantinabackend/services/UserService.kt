package com.example.cantinabackend.services

import com.example.cantinabackend.config.annotations.Permission
import com.example.cantinabackend.config.annotations.RequiredPermissions
import com.example.cantinabackend.domain.dtos.AddressDto
import com.example.cantinabackend.domain.dtos.PermissionDto
import com.example.cantinabackend.domain.dtos.UserChangeDto
import com.example.cantinabackend.domain.dtos.UserDto
import com.example.cantinabackend.domain.entities.Address
import com.example.cantinabackend.domain.entities.User
import com.example.cantinabackend.domain.repositories.AddressRepository
import com.example.cantinabackend.domain.repositories.UserRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val securityAuthenticationService: SecurityAuthenticationService,
    private val userRepository: UserRepository,
    private val addressRepository: AddressRepository
) {

    @RequiredPermissions([Permission.NORMAL_USER])
    @Transactional
    fun findUserOrCreate(): UserDto {
        val userId = securityAuthenticationService.getUserId()

        val user = userRepository.findByIdOrNull(userId) ?: User(userId, null).also { userRepository.save(it) }

        return UserDto(
            user.phoneNumber,
            user.addresses.map {
                AddressDto(
                    it.id,
                    it.value,
                    it.isCurrent
                )
            }.sortedByDescending { it.isCurrent }
        )
    }

    @RequiredPermissions([Permission.NORMAL_USER])
    @Transactional(readOnly = true)
    fun getAllUserPermissions(): PermissionDto {
        return PermissionDto(securityAuthenticationService.getUserPermissions())
    }

    @RequiredPermissions([Permission.NORMAL_USER])
    @Transactional
    fun changeUser(userChanges: UserChangeDto) {
        val user = userRepository.findByIdOrNull(securityAuthenticationService.getUserId())
            ?: throw EntityNotFoundException("User not found")

        user.phoneNumber = userChanges.phoneNumber
    }

    @Transactional
    @RequiredPermissions([Permission.NORMAL_USER])
    fun deleteUserAddress(addressId: Int) {
        val user = userRepository.findByIdOrNull(securityAuthenticationService.getUserId())
            ?: throw EntityNotFoundException("User not found")

        val address = addressRepository.findByIdOrNull(addressId)
            ?: throw EntityNotFoundException("Address not found")

        if (address.user.id != user.id) {
            throw EntityNotFoundException("Address not found")
        }

        addressRepository.delete(address)
    }

    @Transactional
    @RequiredPermissions([Permission.NORMAL_USER])
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun createOrUpdateAddress(address: AddressDto) {

        val user = userRepository.findByIdOrNull(securityAuthenticationService.getUserId())
            ?: throw EntityNotFoundException("User not found")

        when {
            address.id == 0 -> {

                if (address.isCurrent) {
                    addressRepository.setAllAddressesNotCurrent(user.id)
                }

                addressRepository.save(
                    Address(
                        address.value,
                        address.isCurrent,
                        user
                    )
                )
            }

            else -> {
                val addressEntity = addressRepository.findByIdOrNull(address.id)
                    ?: throw EntityNotFoundException("Address not found")

                if (addressEntity.user.id != user.id) {
                    throw EntityNotFoundException("Address doesn't belong to user")
                }

                if (address.isCurrent) {
                    addressRepository.setAllAddressesNotCurrent(user.id)
                }

                addressEntity.value = address.value
                addressEntity.isCurrent = address.isCurrent

                addressRepository.save(addressEntity)
            }
        }

    }

}