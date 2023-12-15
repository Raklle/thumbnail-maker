package kkk.to.services

import kkk.to.models.Image
import kkk.to.repositories.H2Repository
import kkk.to.util.Size
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class DBService @Autowired constructor(
    private val h2Repository: H2Repository
) {
    private var lastTicketID: Long = 0
    fun createTicket(): String {
        lastTicketID += 1
        return lastTicketID.toString()
    }

    fun uploadImage(image: Image): Image {
        return h2Repository.save(image)
    }

    fun uploadImages(images: List<Image>): List<Image> {
        return h2Repository.saveAll(images)
    }

    fun getImages(): MutableList<Image> {
        return h2Repository.findAll()
    }

    fun getImageById(id: Long): Optional<Image> {
        return h2Repository.findById(id)
    }

    fun getImagesByTicket(id: String): MutableList<Image> {
        return h2Repository.findByTicketID(id)
    }

    fun setData(image: Image, data: ByteArray, size: Size) {
        image.imageID?.let { h2Repository.findById(it) }?.takeIf { it.isPresent }?.get()?.apply {
            when (size) {
                Size.SMALL -> small = data
                Size.MEDIUM -> medium = data
                Size.LARGE -> large = data
//                else -> throw IllegalArgumentException("Invalid size: $size")
            }
            h2Repository.save(this)
        }
    }

    fun getImagesToMinimize(size: Size): List<Image>{
        return when (size) {
            Size.SMALL -> h2Repository.findAll().filter{ it.small == null }
            Size.MEDIUM -> h2Repository.findAll().filter{ it.medium == null }
            Size.LARGE -> h2Repository.findAll().filter{ it.large == null }
        }
    }

}