package com.example.cantinabackend.services

import com.example.cantinabackend.config.annotations.Permission
import com.example.cantinabackend.config.annotations.RequiredPermissions
import com.example.cantinabackend.domain.dtos.PermissionDto
import com.example.cantinabackend.domain.dtos.UserChangeDto
import com.example.cantinabackend.domain.entities.Address
import com.example.cantinabackend.domain.entities.User
import com.example.cantinabackend.domain.repositories.AddressRepository
import com.example.cantinabackend.domain.repositories.UserRepository
import jakarta.persistence.EntityNotFoundException
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
    fun findUserOrCreate(): User {
        val userId = securityAuthenticationService.getUserId()

        return userRepository.findByIdOrNull(userId) ?: User(userId, null).also { userRepository.save(it) }
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

        val currentAddresses = user.addresses

        val newAddresses = userChanges.addresses.map {
            val address = currentAddresses.find { address -> address.id == it.id } ?: Address(
                it.value,
                user
            )
            address.value = it.value
            address
        }

        user.addresses.clear()
        user.addresses.addAll(newAddresses)

        user.phoneNumber = userChanges.phoneNumber

        userRepository.save(user)
    }

}