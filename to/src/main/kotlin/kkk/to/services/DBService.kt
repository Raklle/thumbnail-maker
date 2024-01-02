package kkk.to.services

import kkk.to.models.Image
import kkk.to.util.ImageResponse
import kkk.to.util.Size
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DBService {
    fun saveImages(images: Flux<Image>): Flux<Image>
    fun getAllImages(): Flux<ImageResponse>
    fun getAllImagesBySize(size: Size): Flux<ImageResponse>
    fun getImageById(id: String): Mono<ImageResponse>
    fun getImageByIdAndSize(id: String, size: Size): Mono<ImageResponse>
    fun getAllSmallImagesToMinimize(): Flux<Image>
    fun getAllMediumImagesToMinimize(): Flux<Image>
    fun getAllLargeImagesToMinimize(): Flux<Image>

}