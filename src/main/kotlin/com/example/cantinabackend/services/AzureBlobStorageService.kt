package com.example.cantinabackend.services

import com.azure.storage.blob.BlobServiceClientBuilder
import com.example.cantinabackend.domain.repositories.MenuItemRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class AzureBlobStorageService(
    private val menuItemRepository: MenuItemRepository,
) {

    @Value("\${spring.cloud.azure.storage.blob.container-name}")
    private lateinit var containerName: String

    @Value("\${spring.cloud.azure.storage.account-name}")
    private lateinit var accountName: String

    @Value("\${spring.cloud.azure.storage.account-key}")
    private lateinit var accountKey: String

    @Transactional
    fun uploadImage(file: MultipartFile, id: String): String {

        val menuItem =
            menuItemRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("Menu not found")

        val blobName = generateUniqueBlobName(file.originalFilename ?: "unnamed_file")
        val blobUrl = "https://$accountName.blob.core.windows.net/$containerName/$blobName"

        val blobServiceClient = BlobServiceClientBuilder()
            .connectionString("DefaultEndpointsProtocol=https;AccountName=$accountName;AccountKey=$accountKey;EndpointSuffix=core.windows.net")
            .buildClient()

        val containerClient = blobServiceClient.getBlobContainerClient(containerName)

        if (menuItem.photoUrl != null) {
            containerClient.getBlobClient(menuItem.photoUrl?.split("/")?.last()).delete()
        }

        val blobClient = containerClient.getBlobClient(blobName)

        blobClient.upload(file.inputStream, file.size)
        
        menuItem.photoUrl = blobUrl
        return blobUrl

    }

    private fun generateUniqueBlobName(originalFilename: String): String {
        val uuid = UUID.randomUUID()
        return "${uuid}_$originalFilename"
    }
}