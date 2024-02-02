package com.example.cantinabackend.web.swagger

import com.example.cantinabackend.domain.dtos.MissingContainersDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Menu Controller")
interface IMenuController {

    @Operation(
        summary = "Upload menu file from excel",
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