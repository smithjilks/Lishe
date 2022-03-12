package com.smith.lishe.data.reviews.datasource

import com.smith.lishe.model.*
import com.smith.lishe.network.ReviewApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ReviewsRemoteDataSource(
    private val reviewApi: ReviewApi,
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * This executes on an IO-optimized thread pool, the function is main-safe.
     */
    suspend fun fetchAllReviews(): List<ReviewModel> =
    // Move the execution to an IO-optimized thread since the ApiService
        // doesn't support coroutines and makes synchronous reviews.
        withContext(ioDispatcher) {
            reviewApi.retrofitService.getAllReviews()
        }

    suspend fun createNewReview(newReviewDetails: ReviewDetailsModel): ReviewApiModel =
        withContext(ioDispatcher) {
            reviewApi.retrofitService.createNewReview(newReviewDetails)
        }

    suspend fun fetchUserReviews(userId: String): List<ReviewModel> =
        withContext(ioDispatcher) {
            reviewApi.retrofitService.getUserReviews(userId)
        }

    suspend fun fetchOneReview(id: String): ReviewModel =
        withContext(ioDispatcher) {
            reviewApi.retrofitService.getReview(id)
        }

    suspend fun updateReview(reviewUpdateDetails: ReviewDetailsModel): ReviewApiModel =
        withContext(ioDispatcher) {
            reviewApi.retrofitService.updateReview(reviewUpdateDetails)
        }

    suspend fun deleteReview(id: String): ReviewApiModel =
        withContext(ioDispatcher) {
            reviewApi.retrofitService.deleteReview(id)
        }


}
