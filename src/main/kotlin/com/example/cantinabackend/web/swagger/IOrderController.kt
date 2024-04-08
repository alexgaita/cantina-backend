package com.example.cantinabackend.web.swagger

import com.example.cantinabackend.domain.dtos.OrderCreateDto
import com.example.cantinabackend.domain.dtos.OrdersByDateDto
import com.example.cantinabackend.domain.dtos.PaymentIntentDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Order Controller")
interface IOrderController {

    @Operation(
        summary = "Place Order",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = PaymentIntentDto::class
                    )
                )]
        )
    )
    fun placeOrder(@RequestBody order: OrderCreateDto): PaymentIntentDto

    @Operation(
        summary = "Confirm Order Details",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = Unit::class
                    )
                )]
        )
    )
    fun confirmOrderDetails(@PathVariable orderId: Int, @RequestBody address: Int, @RequestBody phoneNumber: String)

    @Operation(
        summary = "Get all orders for user",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = OrdersByDateDto::class
                    )
                )]
        )
    )
    fun getAllOrdersForUser(): OrdersByDateDto

    @Operation(
        summary = "Get all orders",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = OrdersByDateDto::class
                    )
                )]
        )
    )
    fun getAllOrders(): OrdersByDateDto

    @Operation(
        summary = "Delete Order",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = Unit::class
                    )
                )]
        )
    )
    fun deleteOrder(@PathVariable orderId: Int)

    @Operation(
        summary = "Delete Order",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = Unit::class
                    )
                )]
        )
    )
    fun changeOrderStatus(@PathVariable orderId: Int, @RequestParam status: String)

}