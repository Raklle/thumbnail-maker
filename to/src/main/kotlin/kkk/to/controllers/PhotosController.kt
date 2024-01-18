package kkk.to.controllers

import kkk.to.models.Directory
import kkk.to.models.Image
import kkk.to.services.DBService
import kkk.to.util.ImageResponse
import kkk.to.util.Size
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
class PhotosController (private val dbService: DBService) {

    @PostMapping
    fun saveImages(@RequestPart("files") images: Flux<ByteArray>, @RequestPart("path", required = false) path: String?): Flux<String> {
        return dbService.saveImages(images.map{
            image -> Image(original = image, path = path ?: "")
        }).mapNotNull { image -> image.id + "\n"}
    }
    @PostMapping("/directory")
    fun saveDirectory(@RequestPart("path", required = false) path: String?, @RequestPart("name") name: String): Mono<String> {
        return dbService.saveDirectory(Directory(name = name, path = path?: "")).map{ directory -> directory.path + "/" + directory.name }
    }

    @GetMapping("/directory")
    fun getDirectories(@RequestParam("path", required = false) path: String?): Flux<Directory> {
//        return dbService.getDirectories(path?: "").map { directory -> directory.name + ";" }
        return dbService.getDirectories(path?: "")
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
    fun findAllProducts(@RequestParam("size") pageSize: Int, @RequestParam("page") pageNumber: Int,
                        @RequestParam("imgSize") size: String, @RequestParam("offset", required = false) offset: Int?,
                        @RequestParam("path", required = false) path: String?): Flux<ImageResponse> {
        val imgSize  = when(size.uppercase()){
            "SMALL" -> Size.SMALL
            "MEDIUM" -> Size.MEDIUM
            "LARGE" -> Size.LARGE
            else -> throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid value for 'size' parameter. Allowed values are SMALL, MEDIUM, and LARGE."
            )
        }
        return dbService.findAllPageable(PageRequest.of(pageNumber, pageSize), imgSize, offset?: 0, path?: "")
    }


}

