package com.smith.lishe.viewmodel

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.smith.lishe.LoginActivity
import com.smith.lishe.MainActivity
import com.smith.lishe.data.foodlisting.datasource.ListingRemoteDataSource
import com.smith.lishe.data.foodlisting.repository.ListingRepository
import com.smith.lishe.data.requests.datasource.RequestsRemoteDataSource
import com.smith.lishe.data.requests.repository.RequestsRepository
import com.smith.lishe.data.users.datasource.UserRemoteDataSource
import com.smith.lishe.data.users.repository.UserDetailsRepository
import com.smith.lishe.model.ListingModel
import com.smith.lishe.model.RequestApiModel
import com.smith.lishe.model.RequestModel
import com.smith.lishe.model.UserDetailsModel
import com.smith.lishe.network.ListingApi
import com.smith.lishe.network.RequestApi
import com.smith.lishe.network.UserApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class RequestDetailsViewModel  @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(Application()) {

    private var sharedPreferences: SharedPreferences? = null
    private val sharedPrefFile = "com.smith.lishe.user"
    private var token: String? = null

    private val requestId = savedStateHandle.get<String>(MainActivity.REQUEST_ID)
    private val reqUserId = savedStateHandle.get<String>(MainActivity.REQUESTING_USER_ID)
    private val listUserId = savedStateHandle.get<String>(MainActivity.LISTING_USER_ID)


    // Internally, we use a MutableLiveData, because we will be updating the List of MarsPhoto
    // with new values
    private val _request = MutableLiveData<RequestModel>()
    private val _userDetails = MutableLiveData<UserDetailsModel>()
    private val _requestConfirmed = MutableLiveData<Boolean>()
    private val _requestCollected = MutableLiveData<Boolean>()
    private val _requestCancelled = MutableLiveData<Boolean>()



    // The external LiveData interface to the property is immutable, so only this class can modify
    val request: LiveData<RequestModel> = _request
    val userDetails: LiveData<UserDetailsModel> = _userDetails
    val requestConfirmed: LiveData<Boolean> = _requestConfirmed
    val requestCollected: LiveData<Boolean> = _requestCollected
    val requestCancelled: LiveData<Boolean> = _requestCancelled



    /**
     * Call getRequests on init so we can display status immediately.
     */
    init {
        sharedPreferences = application.applicationContext.getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
        token =  sharedPreferences!!.getString(LoginActivity.USER_TOKEN, "")
        val userType =  sharedPreferences!!.getString(LoginActivity.USER_TYPE, "collector")

        viewModelScope.launch {
            try {
                _request.postValue(
                    RequestsRepository(
                        RequestsRemoteDataSource(
                            RequestApi,
                            Dispatchers.IO
                        ), requestId!!
                    ).fetchOneRequest()
                )

                when (userType) {
                    "collector" -> {
                        _userDetails.postValue(
                            UserDetailsRepository(
                                UserRemoteDataSource(
                                    UserApi,
                                    Dispatchers.IO
                                ), listUserId!!
                            ).fetchUserDetails()
                        )
                    }
                    "lister" -> {
                        _userDetails.postValue(
                            UserDetailsRepository(
                                UserRemoteDataSource(
                                    UserApi,
                                    Dispatchers.IO
                                ), reqUserId!!
                            ).fetchUserDetails()
                        )
                    }
                    else -> {
                        throw Exception("Unknown error occurred")
                    }
                }

            } catch (e: Exception) {
                Log.e("Request Details ViewModel", e.toString())
            }
        }
    }


    fun updateRequest(status: RequestBody) {
        Log.e("Request Details ViewModel", token!!)
        viewModelScope.launch {
            try {
                val response = RequestApi.retrofitService.updateRequest("Bearer ${token!!}", status, requestId!!)

                if (response.message == "Update successful")
                    _requestCancelled.postValue(true)

            } catch (e: Exception) {
                _requestCancelled.postValue(false)
                Log.e("Request Status Confirm", e.toString())
            }
        }
    }

}