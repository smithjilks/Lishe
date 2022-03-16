package com.smith.lishe.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.smith.lishe.LoginActivity
import com.smith.lishe.data.users.datasource.UserRemoteDataSource
import com.smith.lishe.data.users.repository.UserDetailsRepository
import com.smith.lishe.model.RequestModel
import com.smith.lishe.model.UserDetailsModel
import com.smith.lishe.network.UserApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    application: Application
): AndroidViewModel(Application()){
    private var sharedPreferences: SharedPreferences? = null
    private val sharedPrefFile = "com.smith.lishe.user"

    private val _user = MutableLiveData<UserDetailsModel>()

    val user: LiveData<UserDetailsModel> = _user

    init {
        sharedPreferences = application.applicationContext.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
        val userId = sharedPreferences!!.getString(LoginActivity.USER_ID, "")

        viewModelScope.launch {
            try {
                _user.postValue(
                    UserDetailsRepository(
                        UserRemoteDataSource(
                            UserApi,
                            Dispatchers.IO
                        ), userId!!
                    ).fetchUserDetails()
                )
            } catch (e: Exception) {
                Log.e("Fetch User Profile Data Error", e.toString())
            }
        }
    }


}