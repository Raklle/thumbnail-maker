package kkk.to.services

import kkk.to.models.Image
import kkk.to.repositories.ImageMongoRepository
import kkk.to.util.ImageResponse
import kkk.to.util.ImageState
import kkk.to.util.Size
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class MongoService @Autowired constructor(
    private val imageMongoRepository: ImageMongoRepository,
    private val mongoTemplate: ReactiveMongoTemplate
) : DBService {
    override fun saveImages(images: Flux<Image>): Flux<Image> {
        return imageMongoRepository.saveAll(images)
    }
    override fun getAllImages(): Flux<ImageResponse> {
        val projectOperation: AggregationOperation = project().
            andExpression("original").`as`("image").
            andExpression("DONE").asLiteral().`as`("state")
        val aggregation: Aggregation = Aggregation.newAggregation(projectOperation)
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
    override fun getAllImagesBySize(size: Size): Flux<ImageResponse> {
//        val matchOperation: MatchOperation = Aggregation.match(Criteria.where("smallState").`is`(ImageState.DONE))
        val projectOperation: AggregationOperation = getProjectionToImageResponse(size)
//        val aggregation: Aggregation = Aggregation.newAggregation(matchOperation, projectOperation)
        val aggregation: Aggregation = Aggregation.newAggregation(projectOperation)
        return mongoTemplate.aggregate(aggregation, "images", ImageResponse::class.java)
    }

    private fun getProjectionToImageResponse(size:Size): ProjectionOperation {
        return when (size) {
                    Size.SMALL -> {
                        project().
                        andExpression("small").`as`("image").
                        andExpression("smallState").`as`("state")
                    }
                    Size.MEDIUM -> {
                        project().
                        andExpression("medium").`as`("image").
                        andExpression("mediumState").`as`("state")
                    }
                    Size.LARGE -> {
                        project().
                        andExpression("large").`as`("image").
                        andExpression("largeState").`as`("state")
                    }
                }

    }

}