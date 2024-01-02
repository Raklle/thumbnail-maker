package kkk.to.repositories

import kkk.to.models.Image
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.stereotype.Repository

@Repository
@EnableMongoRepositories(basePackages = ["kkk.to.repositories"])
interface ImageMongoRepository : ReactiveMongoRepository<Image, String>