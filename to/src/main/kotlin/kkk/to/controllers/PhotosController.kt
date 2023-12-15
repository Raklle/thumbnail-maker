package kkk.to.controllers

import kkk.to.models.Image
import kkk.to.services.DBService
import kkk.to.services.HandlingService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/photos")
class PhotosController (private val dbService: DBService, private val handler: HandlingService) {

    @GetMapping("/test")
    fun testEndpoint(): String {
        return "Testing"
    }

    @PostMapping("/upload")
    @ResponseBody
    fun upload(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        return try {
            val ticketID = dbService.createTicket()
            handler.handleSingleImage(file.bytes, ticketID)

            ResponseEntity.ok("File uploaded successfully! The ticketID is: $ticketID")
        } catch (e: Exception) {
            ResponseEntity.status(500).body("Failed to upload file: ${e.message}")
        }
    }

    @PostMapping("/upload/bulk")
    @ResponseBody
    fun upload(@RequestParam("files") files: List<MultipartFile>): ResponseEntity<String> {
        return try {
            val ticketID = dbService.createTicket()
            handler.handleManyImages(files, ticketID)

            ResponseEntity.ok("Files uploaded successfully! The ticketID is: $ticketID")
        } catch (e: Exception) {
            ResponseEntity.status(500).body("Failed to upload files: ${e.message}")
        }
    }


    @GetMapping("/{imageId}")
    fun getImagesById(
        @PathVariable imageId: Long,
        @RequestParam(required = false, defaultValue = "original") imageSize: String
    ): ResponseEntity<ByteArray> {
        val imageOptional = dbService.getImageById(imageId)

        return if (imageOptional.isPresent) {
            val image = imageOptional.get()
            val imageBytes = image.getImageBySize(imageSize)
            val fileName = "image_${imageSize.lowercase()}.jpg"

            ResponseEntity.ok()
                .header("Content-Disposition", "inline;filename=$fileName")
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes)

        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/tickets/{ticketID}")
    fun getPhotos(
        @PathVariable ticketID: String,
        @RequestParam(required = false, defaultValue = "original") imageSize: String
    ): ResponseEntity<List<ByteArray?>> {
        val images = dbService.getImagesByTicket(ticketID)
        return getResponseForImageList(images, imageSize)
    }

    @GetMapping("/photos")
    fun getAllPhotos(
        @RequestParam(required = false, defaultValue = "original") imageSize: String
    ): ResponseEntity<List<ByteArray?>> {
        val images = dbService.getImages()
        return getResponseForImageList(images, imageSize)
    }

    private fun getResponseForImageList(images: List<Image>, imageSize: String): ResponseEntity<List<ByteArray?>> {
        return if (images.isNotEmpty()) {
            val imageResponses = images.map { image ->
                image.getImageBySize(imageSize)
            }
            ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
                .body(imageResponses)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}