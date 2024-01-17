package kkk.to.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("directories")
data class Directory(

    @Id
    val id: String? = null,
    val path:String = "",
    val name: String = ""

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Directory

        if (path != other.path) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}