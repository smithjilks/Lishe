package com.smith.lishe.data.foodlisting.repository

import com.smith.lishe.data.foodlisting.datasource.ListingRemoteDataSource
import com.smith.lishe.model.ListingModel


class ListingRepository(
    private val listingRemoteDataSource: ListingRemoteDataSource,
    private val id: String
) {

    suspend fun fetchAllListing(): List<ListingModel> =
        listingRemoteDataSource.fetchAllListings()

    suspend fun fetchUserListings(): List<ListingModel> =
        listingRemoteDataSource.fetchUserListings(id)

    suspend fun fetchOneListing(): List<ListingModel> =
        listingRemoteDataSource.fetchUserListings(id)
}