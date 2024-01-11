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
    fun getAllSmallImages(@RequestParam("id", required = false) downloadedImagesId: ArrayList<String>?): Flux<ImageResponse> {
        return if (downloadedImagesId == null) {
            dbService.getAllImagesBySize(Size.SMALL)
        } else {
            dbService.getAllImagesBySize(Size.SMALL).filter { image -> !downloadedImagesId.contains(image.id) }
        }
    }
    @GetMapping("/medium/photos")
    fun getAllMediumImages(@RequestParam("id", required = false) downloadedImagesId: ArrayList<String>?): Flux<ImageResponse> {
        return if (downloadedImagesId == null) {
            dbService.getAllImagesBySize(Size.MEDIUM)
        } else {
            dbService.getAllImagesBySize(Size.MEDIUM).filter { image -> !downloadedImagesId.contains(image.id) }
        }
    }

    @GetMapping("/large/photos")
    fun getAllLargeImages(@RequestParam("id", required = false) downloadedImagesId: ArrayList<String>?): Flux<ImageResponse> {
        return if (downloadedImagesId == null) {
            dbService.getAllImagesBySize(Size.LARGE)
        } else {
            dbService.getAllImagesBySize(Size.LARGE).filter { image -> !downloadedImagesId.contains(image.id) }
        }
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

