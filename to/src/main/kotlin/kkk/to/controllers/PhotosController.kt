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
class PhotosController (private val dbService: DBService, private val h2Repository: H2Repository) {

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


            val image = Image(original = file.bytes)

            // this needs to be pushed to the service
            val savedImage = h2Repository.save(image)

            ResponseEntity.ok("File uploaded successfully! The imageID is: ${savedImage.imageID}")
        } catch (e: Exception) {
            ResponseEntity.status(500).body("Failed to upload file: ${e.message}")
        }
    }

    @GetMapping("/{imageId}", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getImagesById(@PathVariable imageId: Long): ResponseEntity<ByteArray> {
        val image = h2Repository.findByImageID(imageId)

        return if (image != null) {
            ResponseEntity.ok()
                .header("Content-Disposition", "inline;filename=image.jpg")
                .body(image.original)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/tickets/{ticketID}")
    fun getPhotos(@PathVariable ticketID: String) {

    }

    @GetMapping("/photos")
    fun getAllPhotos() {

    }
}