package com.smith.lishe.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.smith.lishe.LoginActivity
import com.smith.lishe.data.foodlisting.datasource.ListingRemoteDataSource
import com.smith.lishe.data.foodlisting.repository.ListingRepository
import com.smith.lishe.data.requests.datasource.RequestsRemoteDataSource
import com.smith.lishe.data.requests.repository.RequestsRepository
import com.smith.lishe.model.ListingModel
import com.smith.lishe.network.ListingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(Application()) {
    private var sharedPreferences: SharedPreferences? = null
    private val sharedPrefFile = "com.smith.lishe.user"

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsPhoto
    // with new values
    private val _listings = MutableLiveData<List<ListingModel>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val listings: LiveData<List<ListingModel>> = _listings

    /**
     * Call getListings on init so we can display status immediately.
     */
    init {
        sharedPreferences = application.applicationContext.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE
        )
        val userId =  sharedPreferences!!.getString(LoginActivity.USER_ID, "")
        val userType =  sharedPreferences!!.getString(LoginActivity.USER_TYPE, "")
        Log.d("Usertype checking", userType.toString())


        viewModelScope.launch {
            try {
                if (userType == "collector") {
                    _listings.postValue(
                        ListingRepository(
                            ListingRemoteDataSource(ListingApi, IO),
                            ""
                        ).fetchAllListing()
                    )
                } else {
                    _listings.postValue(
                        ListingRepository(
                            ListingRemoteDataSource(ListingApi, IO),
                            userId!!
                        ).fetchUserListings()
                    )

                }
            } catch (e: Exception) {
                Log.e("Fetch Lisitngs Error", e.toString())

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