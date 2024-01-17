package kkk.to.services

import kkk.to.models.Directory
import kkk.to.models.Image
import kkk.to.repositories.DirectoryMongoRepository
import kkk.to.repositories.ImageMongoRepository
import kkk.to.util.ImageResponse
import kkk.to.util.ImageState
import kkk.to.util.Size
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.*
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class MongoService @Autowired constructor(
    private val imageMongoRepository: ImageMongoRepository,
    private val directoryMongoRepository: DirectoryMongoRepository,
    private val mongoTemplate: ReactiveMongoTemplate,

) : DBService {
    override fun saveImages(images: Flux<Image>): Flux<Image> {
        return imageMongoRepository.saveAll(images)
    }
    override fun saveDirectory(directory: Directory): Mono<Directory> {
        return directoryMongoRepository.save(directory)
    }

    override fun getAllImages(path: String): Flux<ImageResponse> {
        val matchOperation: MatchOperation = Aggregation.match(Criteria.where("path").`is`(path))
        val projectOperation: AggregationOperation = project().
            andExpression("original").`as`("image").
            andExpression("DONE").asLiteral().`as`("state")
        val aggregation: Aggregation = Aggregation.newAggregation(matchOperation, projectOperation)
        return mongoTemplate.aggregate(aggregation, "images", ImageResponse::class.java)
    }

    override fun getDirectories(path: String): Flux<Directory>{
        val matchOperation: MatchOperation = Aggregation.match(Criteria.where("path").`is`(path))
        val aggregation: Aggregation = Aggregation.newAggregation(matchOperation)
        return mongoTemplate.aggregate(aggregation, "directories", Directory::class.java)
    }
    override fun getAllImagesBySize(size: Size, path: String): Flux<ImageResponse> {
        val matchOperation: MatchOperation = Aggregation.match(Criteria.where("path").`is`(path))
        val projectOperation: AggregationOperation = getProjectionToImageResponse(size)
        val aggregation: Aggregation = Aggregation.newAggregation(matchOperation, projectOperation)
        return mongoTemplate.aggregate(aggregation, "images", ImageResponse::class.java)
    }
    override fun getImageById(id: String): Mono<ImageResponse> {
        val matchOperation: MatchOperation = Aggregation.match(Criteria.where("_id").`is`(id))
        val projectOperation: AggregationOperation = project().
            andExpression("original").`as`("image").
            andExpression("DONE").asLiteral().`as`("state")
        val aggregation: Aggregation = Aggregation.newAggregation(matchOperation, projectOperation)
        return mongoTemplate.aggregate(aggregation, "images", ImageResponse::class.java).next()
    }
    override fun getImageByIdAndSize(id: String, size: Size): Mono<ImageResponse> {
        val matchOperation: MatchOperation = Aggregation.match(Criteria.where("_id").`is`(id))
        val projectOperation: AggregationOperation = getProjectionToImageResponse(size)
        val aggregation: Aggregation = Aggregation.newAggregation(matchOperation, projectOperation)
        return mongoTemplate.aggregate(aggregation, "images", ImageResponse::class.java).next()
    }
    override fun getAllSmallImagesToMinimize(): Flux<Image> {
        val matchOperation: MatchOperation = Aggregation.match(Criteria.where("smallState").`is`(ImageState.TO_MINIMIZE))
        val aggregation: Aggregation = Aggregation.newAggregation(matchOperation)
        return mongoTemplate.aggregate(aggregation, "images", Image::class.java)
    }

    override fun getAllMediumImagesToMinimize(): Flux<Image> {
        val matchOperation: MatchOperation = Aggregation.match(Criteria.where("mediumState").`is`(ImageState.TO_MINIMIZE))
        val aggregation: Aggregation = Aggregation.newAggregation(matchOperation)
        return mongoTemplate.aggregate(aggregation, "images", Image::class.java)
    }

   override fun getAllLargeImagesToMinimize(): Flux<Image> {
       val matchOperation: MatchOperation = Aggregation.match(Criteria.where("largeState").`is`(ImageState.TO_MINIMIZE))
       val aggregation: Aggregation = Aggregation.newAggregation(matchOperation)
       return mongoTemplate.aggregate(aggregation, "images", Image::class.java)
    }

    override fun getAllImagesToMinimize(): Flux<Image> {
        val matchOperation: MatchOperation = Aggregation.match(Criteria.where("smallState").`is`(ImageState.TO_MINIMIZE)
            .orOperator(Criteria.where("mediumState").`is`(ImageState.TO_MINIMIZE))
            .orOperator(Criteria.where("largeState").`is`(ImageState.TO_MINIMIZE)))
        val aggregation: Aggregation = Aggregation.newAggregation(matchOperation)
        //sorted by creation time
        return mongoTemplate.aggregate(aggregation, "images", Image::class.java)
            .sort { obj1, obj2 -> obj1.id?.let { obj2.id?.compareTo(it) } ?: 0 }
    }


    override fun findAllPageable(pageable: Pageable, size: Size, offset:Int, path: String): Flux<ImageResponse> {
        val matchOperation: MatchOperation = Aggregation.match(Criteria.where("path").`is`(path))
        val projectOperation: AggregationOperation = getProjectionToImageResponse(size)
        val skipOperation: SkipOperation = Aggregation.skip(pageable.offset + offset)
        val limitOperation: LimitOperation = Aggregation.limit(pageable.pageSize.toLong() - offset)
        val aggregation: Aggregation = Aggregation.newAggregation(matchOperation, projectOperation, skipOperation, limitOperation)

        return mongoTemplate.aggregate(aggregation, "images", ImageResponse::class.java)
    }

    private fun getProjectionToImageResponse(size:Size): ProjectionOperation {
        return when (size) {
                    Size.SMALL -> {
                        project().
                        andExpression("small").`as`("image").
                        andExpression("smallState").`as`("state").
                        andExpression("path").`as`("path")
                    }
                    Size.MEDIUM -> {
                        project().
                        andExpression("medium").`as`("image").
                        andExpression("mediumState").`as`("state").
                        andExpression("path").`as`("path")
                    }
                    Size.LARGE -> {
                        project().
                        andExpression("large").`as`("image").
                        andExpression("largeState").`as`("state").
                        andExpression("path").`as`("path")
                    }
                }
    }

}