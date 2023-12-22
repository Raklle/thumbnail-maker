package kkk.to.services

import kkk.to.models.Image
import kkk.to.util.Size
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class HandlingService(private val dbService: DBService) {
    private val mnService =  MinimizingService()

//    fun handleSingleImage(fileBytes: ByteArray, ticketID: String){
//        val image = Image(original = fileBytes, ticketID = ticketID)
//        val savedImage = dbService.uploadImage(image)
//
//        val sizes = Flux.just(Size.SMALL, Size.MEDIUM, Size.LARGE)
//
//        sizes.flatMap { size ->
//            Mono.fromCallable { mnService.minimize(savedImage, size) }
//                .flatMap { minimizedImage ->
//                    Mono.fromCallable { dbService.setData(savedImage, minimizedImage, size) }
//                }
//        }.subscribe()
//
//    }

    fun handleManyImages(files: Flux<MultipartFile>, ticketID: String) : Mono<Void> {

        val imageList = files.map { Image(original = it.bytes, ticketID = ticketID) }
        val savedImages = imageList.flatMap { image ->
            Mono.fromCallable { dbService.uploadImage(image) }
        }
//        val savedImages = dbService.uploadImages(imageList)

        val sizes = Flux.just(Size.SMALL, Size.MEDIUM, Size.LARGE)

        val processImage = { image: Image, size: Size ->
            Mono.fromCallable { mnService.minimize(image, size) }
                .flatMap { minimizedImage ->
                    Mono.fromCallable { dbService.setData(image, minimizedImage, size) }
                }
        }

        val imageFlux = savedImages
            .flatMap { image ->
                sizes.flatMap { size -> processImage(image, size) }
            }
            .subscribeOn(Schedulers.parallel())

        return imageFlux.then()
    }

}
