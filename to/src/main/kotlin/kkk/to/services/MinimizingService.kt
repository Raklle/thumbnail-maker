package kkk.to.services

import kkk.to.models.Image
import org.springframework.stereotype.Service

// Probably we will use polymorphism rather than factory
@Service
class MinimizingService {

    //tutaj zamiast image.original wsatwic wywolanie odpowiednie funkcji zmniejszajacej obraz
    fun minimize(image: Image, size: String):ByteArray{
        return when (size) {
            "small" -> image.original
            "medium" -> image.original
            "big" -> image.original
            else -> throw IllegalArgumentException("Invalid size: $size")
        }
    }

}