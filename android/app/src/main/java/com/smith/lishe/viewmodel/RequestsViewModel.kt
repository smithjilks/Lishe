package com.smith.lishe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smith.lishe.data.requests.datasource.RequestsRemoteDataSource
import com.smith.lishe.data.requests.repository.RequestsRepository
import com.smith.lishe.model.RequestModel
import com.smith.lishe.network.RequestApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RequestsViewModel : ViewModel() {
    // Internally, we use a MutableLiveData, because we will be updating the List of MarsPhoto
    // with new values
    private val _requests = MutableLiveData<List<RequestModel>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val requests: LiveData<List<RequestModel>> = _requests

    /**
     * Call getRequests on init so we can display status immediately.
     */
    init {
        viewModelScope.launch {
            try {
                _requests.postValue(RequestsRepository(RequestsRemoteDataSource(RequestApi, Dispatchers.IO), "").fetchAllRequests())
            } catch (e: Exception) {
                Log.e("Requests ViewModel", e.toString())
            }
        }
    }

    /**
     * Gets Food Requests information from the API Retrofit service and updates the
     * [RequestModel] [List] [LiveData].
     */
    private fun getRequests(): List<RequestModel> {

        val data: MutableList<RequestModel> = mutableListOf()
        viewModelScope.launch {
            try {
                data.addAll(RequestsRepository(RequestsRemoteDataSource(RequestApi, Dispatchers.IO), "").fetchAllRequests())

            } catch (e: Exception) {

            }
        }
        return data
    }

}