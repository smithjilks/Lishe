package com.smith.lishe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smith.lishe.data.foodlisting.datasource.ListingRemoteDataSource
import com.smith.lishe.data.foodlisting.repository.ListingRepository
import com.smith.lishe.data.requests.datasource.RequestsRemoteDataSource
import com.smith.lishe.data.requests.repository.RequestsRepository
import com.smith.lishe.model.ListingModel
import com.smith.lishe.network.ListingApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsPhoto
    // with new values
    private val _listings = MutableLiveData<List<ListingModel>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val listings: LiveData<List<ListingModel>> = _listings

    /**
     * Call getListings on init so we can display status immediately.
     */
    init {
        viewModelScope.launch {
            try {
                _listings.postValue(ListingRepository(ListingRemoteDataSource(ListingApi, IO), "").fetchAllListing())

            } catch (e: Exception) {

            }
        }
    }

    /**
     * Gets Food Listings information from the API Retrofit service and updates the
     * [ListingModel] [List] [LiveData].
     */
     private fun getListings(): List<ListingModel> {

        val data: MutableList<ListingModel> = mutableListOf()
        viewModelScope.launch {
            try {
                data.addAll(ListingRepository(ListingRemoteDataSource(ListingApi, IO), "").fetchAllListing())

            } catch (e: Exception) {

            }
        }
        return data
    }

}