package kkk.to.repositories

import kkk.to.models.Image
import org.springframework.data.jpa.repository.JpaRepository
import reactor.core.publisher.Flux

//import org.springframework.data.repository.reactive.ReactiveCrudRepository

//interface H2Repository : ReactiveCrudRepository<Image, String> {
//
//}

interface H2Repository : JpaRepository<Image, Long> {
    fun findByTicketID(ticketID: String): MutableList<Image>
//    fun saveAll(images: Flux<Image>): Flux<Image>
}