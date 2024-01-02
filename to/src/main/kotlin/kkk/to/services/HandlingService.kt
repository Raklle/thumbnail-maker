package kkk.to.services

import jakarta.annotation.PostConstruct
import kkk.to.models.Image
import kkk.to.util.ImageState
import kkk.to.util.Size
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class HandlingService(private val dbService: MongoService, private val mnService: MinimizingService) {

    @PostConstruct
    fun initialize() {
        handleMinimizing()
    }
    fun handleMinimizing(){
        Mono.delay(Duration.ofSeconds(2))
            .repeatWhen { flux -> flux.delayElements(Duration.ofSeconds(2)) }
            .subscribe {
                handleMinimizingSmallImages(dbService.getAllSmallImagesToMinimize()).subscribe()
                handleMinimizingMediumImages(dbService.getAllMediumImagesToMinimize()).subscribe()
                handleMinimizingLargeImages(dbService.getAllLargeImagesToMinimize()).subscribe()
            }
    }

    fun handleMinimizingSmallImages(images: Flux<Image>): Flux<Image> {
        return dbService.saveImages(
            images.flatMap { image ->
                val minimizedImage = mnService.minimize(image, Size.SMALL)
                if (minimizedImage != null) {
                    Flux.just(image.copy(small = minimizedImage, smallState = ImageState.DONE))
                } else {
                    Flux.just(image.copy(smallState = ImageState.FAILED))
                }
            }
        )
    }

    fun handleMinimizingMediumImages(images: Flux<Image>): Flux<Image> {
        return dbService.saveImages(
            images.flatMap { image ->
                val minimizedImage = mnService.minimize(image, Size.MEDIUM)
                if (minimizedImage != null) {
                    Flux.just(image.copy(medium = minimizedImage, mediumState = ImageState.DONE))
                } else {
                    Flux.just(image.copy(mediumState = ImageState.FAILED))
                }
            }
        )
    }

    fun handleMinimizingLargeImages(images: Flux<Image>): Flux<Image> {
        return dbService.saveImages(
            images.flatMap { image ->
                val minimizedImage = mnService.minimize(image, Size.LARGE)
                if (minimizedImage != null) {
                    Flux.just(image.copy(large = minimizedImage, largeState = ImageState.DONE))
                } else {
                    Flux.just(image.copy(largeState = ImageState.FAILED))
                }
            }
        )
    }

}
