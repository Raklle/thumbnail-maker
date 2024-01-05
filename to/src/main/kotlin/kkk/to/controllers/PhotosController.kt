package kkk.to.controllers

import kkk.to.models.Image
import kkk.to.services.DBService
import kkk.to.util.ImageResponse
import kkk.to.util.Size
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class PhotosController (private val dbService: DBService) {

    @PostMapping
        fun saveImages(@RequestPart("files") images: Flux<ByteArray>): Flux<String> {
        return dbService.saveImages(images.map{
            image -> Image(original = image)
        }).mapNotNull { image -> image.id + "\n"}
    }

    @GetMapping
    fun getAllImages(): Flux<ImageResponse> {
        return dbService.getAllImages()
    }

    @GetMapping("/{id}")
    fun getImageById(@PathVariable id: String): Mono<ImageResponse> {
        return dbService.getImageById(id)
    }

    @GetMapping("/small/photos")
    fun getAllSmallImages(): Flux<ImageResponse> {
        return dbService.getAllImagesBySize(Size.SMALL)
    }
    @GetMapping("/medium/photos")
    fun getAllMediumImages(): Flux<ImageResponse> {
        return dbService.getAllImagesBySize(Size.MEDIUM)
    }
    @GetMapping("/large/photos")
    fun getAllLargeImages(): Flux<ImageResponse> {
        return dbService.getAllImagesBySize(Size.LARGE)
    }
    @GetMapping("/small/{id}")
    fun getSmallImageById(@PathVariable id: String): Mono<ImageResponse> {
        return dbService.getImageByIdAndSize(id, Size.SMALL)
    }

    @GetMapping("/medium/{id}")
    fun getMediumImageById(@PathVariable id: String): Mono<ImageResponse> {
        return dbService.getImageByIdAndSize(id, Size.MEDIUM)
    }
    @GetMapping("/large/{id}")
    fun getLargeImageById(@PathVariable id: String): Mono<ImageResponse> {
        return dbService.getImageByIdAndSize(id, Size.LARGE)
    }

}

