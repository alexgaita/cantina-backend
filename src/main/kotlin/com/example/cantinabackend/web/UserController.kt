package com.example.cantinabackend.web

import com.example.cantinabackend.domain.dtos.*
import com.example.cantinabackend.services.UserService
import com.example.cantinabackend.web.swagger.IUserController
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
@Validated
class UserController(
    private val userService: UserService
) : IUserController {

    @GetMapping()
    override fun getUser(): UserDto = userService.findUserOrCreate()

    @GetMapping("/permissions")
    override fun getUserPermissions(): PermissionDto = userService.getAllUserPermissions()

    @DeleteMapping("/address/{addressId}")
    fun deleteUserAddress(@PathVariable addressId: Int) = userService.deleteUserAddress(addressId)

    @PutMapping("/address")
    fun createOrUpdateAddress(@RequestBody address: AddressDto) = userService.createOrUpdateAddress(address)

    @PutMapping()
    override fun changeUser(@RequestBody userChanges: UserChangeDto) = userService.changeUser(userChanges)

    @PostMapping("/discounts")
    override fun buyDiscounts(@RequestBody discountOrder: CartelaOrderDto): String =
        userService.placeOrderForDiscounts(discountOrder)

}