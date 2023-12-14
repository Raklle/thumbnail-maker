package kkk.to.services

import kkk.to.models.Image
import kkk.to.repositories.H2Repository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

@Service
class DBService @Autowired constructor(
    private val h2Repository: H2Repository
) {

    fun createTicket(photos: List<Image>): String {
        return ""
    }

    fun updatePhoto(id: String, photo: Image) {

    }

//    fun saveImage(file: Mono<FilePart>): Mono<Images> {
//        return file.flatMap { part ->
//            part.content().collectList().map { dataBuffers ->
//                val byteArray = dataBuffers.flatMap { it.asByteBuffer().toByteArray().toList() }.toByteArray()
//                val imageEntity = Images(original = byteArray)
//                h2Repository.save(imageEntity)
//            }.flatMap { it }
//        }
//    }

//    fun saveImage(file: FilePart): Image {
//
//    }

//    fun getPhoto(id: String): Images? {
//
//    }
}