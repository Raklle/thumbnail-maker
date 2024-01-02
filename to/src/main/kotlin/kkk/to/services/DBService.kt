package kkk.to.services

import kkk.to.models.Image
import kkk.to.repositories.ImageMongoRepository
import kkk.to.util.ImageState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DBService @Autowired constructor(
    private val imageMongoRepository: ImageMongoRepository
) {
    fun saveImages(images: Flux<Image>): Flux<Image> {
        return imageMongoRepository.saveAll(images)
    }
    fun getAllImages(): Flux<Image> {
        return imageMongoRepository.findAll()
    }
    fun getImageById(id: String): Mono<Image> {
        return imageMongoRepository.findById(id)
    }
    fun getAllSmallImagesToMinimize(): Flux<Image> {
        return imageMongoRepository.findAll().filter{
            image -> image.smallState == ImageState.TO_MINIMIZE
        }
    }

    fun getAllMediumImagesToMinimize(): Flux<Image> {
        return imageMongoRepository.findAll().filter{
                image -> image.mediumState == ImageState.TO_MINIMIZE
        }
    }

   fun getAllLargeImagesToMinimize(): Flux<Image> {
        return imageMongoRepository.findAll().filter{
                image -> image.largeState == ImageState.TO_MINIMIZE
        }
    }
}