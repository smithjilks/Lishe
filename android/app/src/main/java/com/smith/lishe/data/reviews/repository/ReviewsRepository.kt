package com.smith.lishe.data.reviews.repository

import com.smith.lishe.data.reviews.datasource.ReviewsRemoteDataSource
import com.smith.lishe.model.RequestModel
import com.smith.lishe.model.ReviewModel


class ReviewsRepository(
    private val reviewsRemoteDataSource: ReviewsRemoteDataSource,
    private val id: String
) {
    suspend fun fetchAllReviews(): List<ReviewModel> =
        reviewsRemoteDataSource.fetchAllReviews()

    suspend fun fetchUserReviews(): List<ReviewModel> =
        reviewsRemoteDataSource.fetchUserReviews(id)

    suspend fun fetchOneRequest(): ReviewModel =
        reviewsRemoteDataSource.fetchOneReview(id)
}