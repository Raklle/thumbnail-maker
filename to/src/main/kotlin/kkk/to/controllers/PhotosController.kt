package kkk.to.controllers

import kkk.to.models.Image
import kkk.to.services.DBService
import kkk.to.services.HandlingService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/photos")
class PhotosController (private val dbService: DBService, private val handler: HandlingService) {

    @GetMapping("/test")
    fun testEndpoint(): String {
        return "Testing"
    }

//    @PostMapping("/upload")
//    @ResponseBody
//    fun upload(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
//        return try {
//            val ticketID = dbService.createTicket()
//            handler.handleSingleImage(file.bytes, ticketID)
//
//            ResponseEntity.ok("File uploaded successfully! The ticketID is: $ticketID")
//        } catch (e: Exception) {
//            ResponseEntity.status(500).body("Failed to upload file: ${e.message}")
//        }
//    }

    @PostMapping
    @ResponseBody
    fun upload(@RequestParam("files") files: List<MultipartFile>): Mono<ResponseEntity<String>> {
        return Mono.fromCallable {
            try {
                val ticketID = dbService.createTicket()
                handler.handleManyImages(files, ticketID)

                ResponseEntity.ok("Files uploaded successfully! The ticketID is: $ticketID")
            } catch (e: Exception) {
                ResponseEntity.status(500).body("Failed to upload files: ${e.message}")
            }
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
            val imageBytes = getImageBySize(image, imageSize)
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

        return if (images.isNotEmpty()) {
            val imageResponses = images.map { image ->
                getImageBySize(image, imageSize)
            }
            ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
                .body(imageResponses)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/photos")
    fun getAllPhotos(
        @RequestParam(required = false, defaultValue = "original") imageSize: String
    ): ResponseEntity<List<ByteArray?>> {
        val images = dbService.getImages()

        return if (images.isNotEmpty()) {
            val imageResponses = images.map { image ->
                getImageBySize(image, imageSize)
            }
            ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
                .body(imageResponses)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    private fun getImageBySize(image: Image, imageSize: String): ByteArray? {
        return when (imageSize.uppercase()) {
            "SMALL" -> image.small
            "MEDIUM" -> image.medium
            "LARGE" -> image.large
            else -> image.original
        }
    }
}