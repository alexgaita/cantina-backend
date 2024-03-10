package com.example.cantinabackend.web.swagger

import com.example.cantinabackend.domain.dtos.MenuItemDto
import com.example.cantinabackend.domain.dtos.MenuItemEditDto
import com.example.cantinabackend.domain.dtos.MenuListDto
import com.example.cantinabackend.domain.dtos.MissingContainersDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Menu Controller")
interface IMenuController {

    @Operation(
        summary = "Fetch the menu for the current day",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Menu successfully received.",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = MenuListDto::class
                    )
                )]
        )
    )
    fun getMenu(): MenuListDto

    @Operation(
        summary = "Upload menu file from excel",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = MissingContainersDto::class
                    )
                )]
        )
    )
    fun handleFileUpload(@RequestParam("file") file: MultipartFile): MissingContainersDto

    @Operation(
        summary = "Fetch the menu by id",
        security = [SecurityRequirement(name = "bearer-key")],
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Menu successfully received.",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = MenuItemDto::class
                    )
                )]
        )
    )
    fun getMenuById(@PathVariable id: String): MenuItemEditDto

    @Operation(
        summary = "Create or Update Menu Item",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Menu Item successfully created or updated.",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = Unit::class
                    )
                )]
        )
    )
    fun createOrUpdateMenuItem(menuItemDto: MenuItemDto)

    @Operation(
        summary = "Delete Menu Item",
        security = [SecurityRequirement(name = "bearer-key")]
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Menu Item successfully deleted.",
            content = [
                Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(
                        implementation = Unit::class
                    )
                )]
        )
    )
    fun deleteMenuItem(@PathVariable id: String)
}