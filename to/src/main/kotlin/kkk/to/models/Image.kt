package kkk.to.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Lob
import jakarta.persistence.Id

@Entity
data class Image(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var imageID: Long? = null,

    val ticketID: String? = null,
    @Lob
    val original: ByteArray,
    @Lob
    var small: ByteArray? = null,
    @Lob
    var medium: ByteArray? = null,
    @Lob
    var big: ByteArray? = null
) {

    // autogenerated by IntelliJ
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (imageID != other.imageID) return false
        if (ticketID != other.ticketID) return false
        if (!original.contentEquals(other.original)) return false
        if (small != null) {
            if (other.small == null) return false
            if (!small.contentEquals(other.small)) return false
        } else if (other.small != null) return false
        if (medium != null) {
            if (other.medium == null) return false
            if (!medium.contentEquals(other.medium)) return false
        } else if (other.medium != null) return false
        if (big != null) {
            if (other.big == null) return false
            if (!big.contentEquals(other.big)) return false
        } else if (other.big != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imageID?.hashCode() ?: 0
        result = 31 * result + (ticketID?.hashCode() ?: 0)
        result = 31 * result + original.contentHashCode()
        result = 31 * result + (small?.contentHashCode() ?: 0)
        result = 31 * result + (medium?.contentHashCode() ?: 0)
        result = 31 * result + (big?.contentHashCode() ?: 0)
        return result
    }
}
