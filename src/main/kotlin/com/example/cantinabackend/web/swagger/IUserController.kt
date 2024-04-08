package com.example.cantinabackend.web.swagger

import com.example.cantinabackend.domain.dtos.CartelaOrderDto
import com.example.cantinabackend.domain.dtos.PermissionDto
import com.example.cantinabackend.domain.dtos.UserChangeDto
import com.example.cantinabackend.domain.dtos.UserDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "User Controller")
interface IUserController {

    @Operation(
        summary = "Fetch the currently logged in user",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "User successfully received.",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = UserDto::class
                    )
                )]
        )
    )
    fun getUser(): UserDto

    @Operation(
        summary = "Fetch the permissions of the currently logged in user",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = PermissionDto::class
                    )
                )]
        )
    )
    fun getUserPermissions(): PermissionDto

    @Operation(
        summary = "Change the currently logged in user",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "User successfully changed.",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = Unit::class
                    )
                )]
        )
    )
    fun changeUser(@RequestBody @Valid userChanges: UserChangeDto): Unit

    @Operation(
        summary = "Buy discounts for user",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "User discount order placed successfully.",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = String::class
                    )
                )]
        )
    )
    fun buyDiscounts(@RequestBody discountOrder: CartelaOrderDto): String

}