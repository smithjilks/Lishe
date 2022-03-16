package com.smith.lishe.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.smith.lishe.LoginActivity
import com.smith.lishe.MainActivity
import com.smith.lishe.data.foodlisting.datasource.ListingRemoteDataSource
import com.smith.lishe.data.foodlisting.repository.ListingRepository
import com.smith.lishe.data.users.datasource.UserRemoteDataSource
import com.smith.lishe.data.users.repository.UserDetailsRepository
import com.smith.lishe.model.ListingModel
import com.smith.lishe.model.RequestDetailsModel
import com.smith.lishe.model.UserDetailsModel
import com.smith.lishe.network.ListingApi
import com.smith.lishe.network.RequestApi
import com.smith.lishe.network.UserApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class FoodListingDetailsViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(Application()) {

    private var sharedPreferences: SharedPreferences? = null
    private val sharedPrefFile = "com.smith.lishe.user"
    private var userId: String? = null
    private var token: String? = null


    private val listingId = savedStateHandle.get<String>(MainActivity.LISTING_ID)
    private val listingUserId = savedStateHandle.get<String>(MainActivity.LISTING_USER_ID)

    private val _listing = MutableLiveData<ListingModel>()
    private val _userDetails = MutableLiveData<UserDetailsModel>()
    private val _requestStatus = MutableLiveData<Boolean>()


    // The external LiveData interface to the property is immutable, so only this class can modify
    val listing: LiveData<ListingModel> = _listing
    val userDetails: LiveData<UserDetailsModel> = _userDetails
    val requestStatus: LiveData<Boolean> = _requestStatus


    init {
        sharedPreferences = application.applicationContext.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE
        )
        userId =  sharedPreferences!!.getString(LoginActivity.USER_ID, "")
        token =  sharedPreferences!!.getString(LoginActivity.USER_TOKEN, "")


        viewModelScope.launch {
            try {
                _listing.postValue(
                    ListingRepository(
                        ListingRemoteDataSource(
                            ListingApi,
                            Dispatchers.IO
                        ), listingId!!
                    ).fetchOneListing()
                )
                _userDetails.postValue(
                    UserDetailsRepository(
                        UserRemoteDataSource(
                            UserApi,
                            Dispatchers.IO
                        ), listingUserId!!
                    ).fetchUserDetails()
                )
                Log.d("User Details Observer", userDetails.value.toString())
            } catch (e: Exception) {
                Log.e("Fetch Detail Exception", e.toString())
            }
        }
    }

    /**
     * Gets Food Listing information from the API Retrofit service and updates the
     * [ListingModel]  [LiveData].
     */
    private fun getListing(id: String): ListingModel {

        var data: ListingModel? = null
        viewModelScope.launch {
            try {
                data = ListingRepository(
                    ListingRemoteDataSource(ListingApi, Dispatchers.IO),
                    id
                ).fetchOneListing()

            } catch (e: Exception) {

            }
        }
        return data!!
    }

    fun requestListing() {
        val requestInfo = RequestDetailsModel(listingId!!, userId!!)
        viewModelScope.launch {
            try {
                val response = RequestApi.retrofitService.createNewRequest("Bearer $token", requestInfo)

                if (response != null)
                    _requestStatus.postValue(true)

            } catch (e: Exception) {
                _requestStatus.postValue(false)
                Log.e("Request Status Confirm", e.toString())
            }
        }
    }

}