package com.example.cantinabackend.web.swagger

import com.example.cantinabackend.domain.dtos.MenuDto
import com.example.cantinabackend.domain.dtos.MissingContainersDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
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
                        implementation = MenuDto::class
                    )
                )]
        )
    )
    fun getMenu(): MenuDto

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
}