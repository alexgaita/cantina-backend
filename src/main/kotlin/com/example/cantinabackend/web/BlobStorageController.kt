package com.example.cantinabackend.web

import com.example.cantinabackend.services.AzureBlobStorageService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ImageController(
    private val azureBlobStorageService: AzureBlobStorageService
) {

    @PostMapping("/uploadImage/{id}")
    fun uploadImage(
        @RequestPart("file") file: MultipartFile,
        @PathVariable("id") id: String,
    ): String {
        return azureBlobStorageService.uploadImage(file, id)
    }
}