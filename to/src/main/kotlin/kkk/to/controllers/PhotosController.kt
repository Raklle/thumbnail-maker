package kkk.to.controllers

import kkk.to.models.Image
import kkk.to.services.DBService
import kkk.to.services.HandlingService
import kkk.to.util.ImageResponse
import kkk.to.util.ImageState
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
    fun getAllImages(): Flux<ImageResponse> {
        return dbService.getAllImages().map { image ->
            ImageResponse(image.id, ImageState.DONE, image.original)
        }
    }

    @GetMapping("/{id}")
    fun getImageById(@PathVariable id: String): Mono<ImageResponse> {
        return dbService.getImageById(id).map { image ->
            ImageResponse(image.id, ImageState.DONE, image.original)
        }
    }

    @GetMapping("/small/photos")
    fun getAllSmallImages(): Flux<ImageResponse> {
        return dbService.getAllImages().mapNotNull { image ->
            image.small?.let { ImageResponse(image.id, image.smallState, it) }
        }
    }

    @GetMapping("/small/{id}")
    fun getSmallImageById(@PathVariable id: String): Mono<ImageResponse> {
        return dbService.getImageById(id).mapNotNull { image ->
            image.small?.let { ImageResponse(image.id, image.smallState, it) }
        }
    }

    @GetMapping("/medium/photos")
    fun getAllMediumImages(): Flux<ImageResponse> {
        return dbService.getAllImages().mapNotNull { image ->
            image.medium?.let { ImageResponse(image.id, image.mediumState, it) }
        }
    }

    @GetMapping("/medium/{id}")
    fun getMediumImageById(@PathVariable id: String): Mono<ImageResponse> {
        return dbService.getImageById(id).mapNotNull { image ->
            image.medium?.let { ImageResponse(image.id, image.mediumState, it) }
        }
    }

    @GetMapping("/large/photos")
    fun getAllLargeImages(): Flux<ImageResponse> {
        return dbService.getAllImages().mapNotNull { image ->
            image.large?.let { ImageResponse(image.id, image.largeState, it) }
        }
    }

    @GetMapping("/large/{id}")
    fun getLargeImageById(@PathVariable id: String): Mono<ImageResponse> {
        return dbService.getImageById(id).mapNotNull { image ->
            image.large?.let { ImageResponse(image.id, image.largeState, it) }
        }
    }
}