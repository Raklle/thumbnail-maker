package kkk.to.controllers

import kkk.to.models.Image
import kkk.to.services.DBService
import kkk.to.util.ImageResponse
import kkk.to.util.ImageState
import kkk.to.util.Size
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
class PhotosController (private val dbService: DBService) {

    @PostMapping
        fun saveImages(@RequestPart("files") images: Flux<ByteArray>, @RequestPart("path") path: String): Flux<String> {
        return dbService.saveImages(images.map{
            image -> Image(original = image, path = path)
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
    fun getAllSmallImages(@RequestParam("id", required = false, ) downloadedImagesId: ArrayList<String>?, @RequestParam("path", required = false) path: String?): Flux<ImageResponse> {
        return if (downloadedImagesId == null) {
            dbService.getAllImagesBySize(Size.SMALL, path ?: "")
        } else {
            dbService.getAllImagesBySize(Size.SMALL, path ?: "").filter { image -> !downloadedImagesId.contains(image.id) }
        }
    }
    @GetMapping("/medium/photos")
    fun getAllMediumImages(@RequestParam("id", required = false) downloadedImagesId: ArrayList<String>?, @RequestParam("path", required = false) path: String?): Flux<ImageResponse> {
        return if (downloadedImagesId == null) {
            dbService.getAllImagesBySize(Size.MEDIUM, path ?: "")
        } else {
            dbService.getAllImagesBySize(Size.MEDIUM, path ?: "").filter { image -> !downloadedImagesId.contains(image.id) }
        }
    }

    @GetMapping("/large/photos")
    fun getAllLargeImages(@RequestParam("id", required = false) downloadedImagesId: ArrayList<String>?, @RequestParam("path", required = false) path: String?): Flux<ImageResponse> {
        return if (downloadedImagesId == null) {
            dbService.getAllImagesBySize(Size.LARGE, path ?: "")
        } else {
            dbService.getAllImagesBySize(Size.LARGE, path ?: "").filter { image -> !downloadedImagesId.contains(image.id) }
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

    @GetMapping("/photos")
    fun findAllProducts(@RequestParam("size") pageSize: Int, @RequestParam("page") pageNumber: Int, @RequestParam("imgSize") size: String): Flux<ImageResponse> {
        val imgSize  = when(size.uppercase()){
            "SMALL" -> Size.SMALL
            "MEDUIM" -> Size.MEDIUM
            "LARGE" -> Size.LARGE
            //chyba by trzeba jakis blad rzucic idk
            else -> Size.SMALL
        }
        return dbService.findAllPageable(PageRequest.of(pageNumber, pageSize), imgSize)
    }

    //moze lepiej zeby to byl get ale latwiej mi bylo skopiowac
    @PostMapping("/add_directory_xd_test")
    fun saveDirectory(@RequestPart("path") path: Flux<String>): Flux<String> {
        return dbService.saveImages(path.map{
                directory -> Image(original = ByteArray(0), path = directory, smallState = ImageState.DIRECTORY, mediumState = ImageState.DIRECTORY, largeState = ImageState.DIRECTORY)
        }).mapNotNull { image -> image.id + "\n"}
    }
}

