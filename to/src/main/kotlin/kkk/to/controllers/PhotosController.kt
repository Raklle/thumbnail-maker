package kkk.to.controllers

import kkk.to.models.Image
import kkk.to.repositories.H2Repository
import kkk.to.services.DBService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI
import java.util.*

@RestController
@RequestMapping("/photos")
class PhotosController (private val dbService: DBService) {

    @GetMapping("/test")
    fun testEndpoint(): String {
        return "Testing"
    }

//    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
//    fun upload(@RequestPart("photos") photos: Mono<FilePart>): ResponseEntity<String> {
//        val savedImage = dbService.saveImage(photos).block()
//        return ResponseEntity.created(URI.create("/images/${savedImage?.imageID}")).body("Image uploaded successfully")
//    }

    @PostMapping("/upload")
    @ResponseBody
    fun upload(@RequestParam("file") file: MultipartFile, ): ResponseEntity<String> {
        return try {
            val ticketID = dbService.createTicket()
            val image = Image(original = file.bytes, ticketID = ticketID)
            val savedImage = dbService.uploadImage(image)

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
            val imageList = files.map { Image(original = it.bytes, ticketID = ticketID) }
            val savedImages = dbService.uploadImages(imageList)

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
        return when (imageSize.lowercase()) {
            "original" -> image.original
            "small" -> image.small
            "medium" -> image.medium
            "big" -> image.big
            else -> image.original
        }
    }
}