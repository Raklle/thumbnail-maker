package kkk.to.controllers

import kkk.to.models.Image
import kkk.to.services.DBService
import kkk.to.services.HandlingService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class PhotosController (private val dbService: DBService) {

    @GetMapping("/test")
    fun testEndpoint(): String {
        return "Testing"
    }

    @PostMapping
    fun saveImages(@RequestParam("files") images: ArrayList<MultipartFile>): Flux<String> {
        return dbService.saveImages(Flux.fromIterable(images).map{
            image -> Image(original = image.bytes)
        }).mapNotNull { image -> image.id + "\n"}
    }

    @GetMapping("/photos")
    fun getAllImages(): Flux<ByteArray> {
        return dbService.getAllImages().map { image -> image.original }
    }

    @GetMapping("/{id}")
    fun getImageById(@PathVariable id: String): Mono<ByteArray> {
        return dbService.getImageById(id).map { image -> image.original }
    }

    @GetMapping("/small/photos")
    fun getAllSmallImages(): Flux<ByteArray> {
        return dbService.getAllImages().mapNotNull { image -> image.small }
    }

    @GetMapping("/small/{id}")
    fun getSmallImageById(@PathVariable id: String): Mono<ByteArray> {
        return dbService.getImageById(id).mapNotNull { image -> image.small }
    }

    @GetMapping("/medium/photos")
    fun getAllMediumImages(): Flux<ByteArray> {
        return dbService.getAllImages().mapNotNull { image -> image.medium }
    }

    @GetMapping("/medium/{id}")
    fun getMediumImageById(@PathVariable id: String): Mono<ByteArray> {
        return dbService.getImageById(id).mapNotNull { image -> image.medium }
    }

    @GetMapping("/large/photos")
    fun getAllLargeImages(): Flux<ByteArray> {
        return dbService.getAllImages().mapNotNull { image -> image.large }
    }

    @GetMapping("/large/{id}")
    fun getLargeImageById(@PathVariable id: String): Mono<ByteArray> {
        return dbService.getImageById(id).mapNotNull { image -> image.large }
    }
}