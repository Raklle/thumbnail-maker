package kkk.to.repositories

import kkk.to.models.Image
import org.springframework.data.jpa.repository.JpaRepository

//interface H2Repository : ReactiveCrudRepository<Images, String> {
//
//}

interface H2Repository : JpaRepository<Image, Long> {
    fun findByImageID(imageID: Long): Image?
}