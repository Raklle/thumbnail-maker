package kkk.to.models

import kkk.to.util.ImageState
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("images")
data class Image(

    @Id
    val id: String? = null,

    val original: ByteArray,
    val path: String,
    var small: ByteArray? = null,
    var medium: ByteArray? = null,
    var large: ByteArray? = null,

    var smallState: ImageState = ImageState.TO_MINIMIZE,
    var mediumState: ImageState = ImageState.TO_MINIMIZE,
    var largeState: ImageState = ImageState.TO_MINIMIZE,

) {

//     fun getImageBySize(imageSize: String): ByteArray? {
//        return when (imageSize.uppercase()) {
//            "SMALL" -> small
//            "MEDIUM" -> medium
//            "LARGE" -> large
//            else -> original
//        }
//    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (id != other.id) return false
        if (!original.contentEquals(other.original)) return false
        if (small != null) {
            if (other.small == null) return false
            if (!small.contentEquals(other.small)) return false
        } else if (other.small != null) return false
        if (medium != null) {
            if (other.medium == null) return false
            if (!medium.contentEquals(other.medium)) return false
        } else if (other.medium != null) return false
        if (large != null) {
            if (other.large == null) return false
            if (!large.contentEquals(other.large)) return false
        } else if (other.large != null) return false
        if (smallState != other.smallState) return false
        if (mediumState != other.mediumState) return false
        if (largeState != other.largeState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + original.contentHashCode()
        result = 31 * result + (small?.contentHashCode() ?: 0)
        result = 31 * result + (medium?.contentHashCode() ?: 0)
        result = 31 * result + (large?.contentHashCode() ?: 0)
        result = 31 * result + smallState.hashCode()
        result = 31 * result + mediumState.hashCode()
        result = 31 * result + largeState.hashCode()
        return result
    }
}
