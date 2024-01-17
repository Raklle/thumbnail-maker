package kkk.to.repositories

import kkk.to.models.Directory
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.stereotype.Repository



@Repository
@EnableMongoRepositories(basePackages = ["kkk.to.repositories"])
interface DirectoryMongoRepository : ReactiveMongoRepository<Directory, String>{

}