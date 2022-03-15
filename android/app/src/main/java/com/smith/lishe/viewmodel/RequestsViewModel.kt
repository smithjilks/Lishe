package com.smith.lishe.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.smith.lishe.LoginActivity
import com.smith.lishe.data.requests.datasource.RequestsRemoteDataSource
import com.smith.lishe.data.requests.repository.RequestsRepository
import com.smith.lishe.model.RequestModel
import com.smith.lishe.network.RequestApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(Application()) {

    private var sharedPreferences: SharedPreferences? = null
    private val sharedPrefFile = "com.smith.lishe.user"

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsPhoto
    // with new values
    private val _requests = MutableLiveData<List<RequestModel>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val requests: LiveData<List<RequestModel>> = _requests

    /**
     * Call getRequests on init so we can display status immediately.
     */
    init {
        sharedPreferences = application.applicationContext.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
        val userId = sharedPreferences!!.getString(LoginActivity.USER_ID, "")
        val userType = sharedPreferences!!.getString(LoginActivity.USER_TYPE, "")

        viewModelScope.launch {
            try {
                if (userType == "collector") {
                    _requests.postValue(
                        RequestsRepository(
                            RequestsRemoteDataSource(
                                RequestApi,
                                Dispatchers.IO
                            ), userId!!
                        ).fetchUserRequests()
                    )
                } else {
                    _requests.postValue(
                        RequestsRepository(
                            RequestsRemoteDataSource(
                                RequestApi,
                                Dispatchers.IO
                            ), userId!!
                        ).fetchAllRequests().filter { it.listingDetails[0].creator == userId }
                    )
                }
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
                data.addAll(
                    RequestsRepository(
                        RequestsRemoteDataSource(RequestApi, Dispatchers.IO),
                        ""
                    ).fetchAllRequests()
                )

            } catch (e: Exception) {

            }
        }
        return data
    }

}