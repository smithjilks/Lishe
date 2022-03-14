package com.smith.lishe

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.enums.PNLogVerbosity
import com.smith.lishe.databinding.ActivityMainBinding
import com.smith.lishe.viewmodel.MainActivityViewModel
import com.smith.lishe.viewmodel.RequestDetailsViewModel


class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var pubNub: PubNub

    private var sharedPreferences: SharedPreferences? = null
    private val sharedPrefFile = "com.smith.lishe.user"
    private var userId: String? = null

    companion object {
        const val LISTING_ID = "listingId"
        const val LISTING_USER_ID = "listingUserId"
        const val REQUEST_ID = "requestId"
        const val REQUESTING_USER_ID = "requestingUserId"
        const val BASE_URL = "https://f27c-197-232-61-236.ngrok.io/api/v1/"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
        userId =  sharedPreferences!!.getString(LoginActivity.USER_ID, "")

        initializePubNub()

        supportFragmentManager.beginTransaction().apply {
            replace(binding.navHostFragment.id, RequestsFragment())
            addToBackStack(null)
            commit()
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            var selectedFragment: Fragment? = null
            when (it.itemId) {
                R.id.menu_home -> {
                    selectedFragment = HomeFragment()
                }
                R.id.menu_history -> {
                    selectedFragment = RequestsFragment()
                }
                R.id.menu_profile -> {
                    selectedFragment = UserProfileFragment()
                }
            }
            if (selectedFragment != null) {
                changeFragment(selectedFragment)
            };

            return@setOnItemSelectedListener true
        }

    }

    private fun changeFragment(fragmentToChange: Fragment): Unit {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.navHostFragment.id, fragmentToChange)
            addToBackStack(null)
            commit()
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { //Can add more as per requirement
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                123
            )
        }
    }

    override fun onDestroy() {
        destroyPubNub()
        super.onDestroy()
    }

    private fun initializePubNub(){
        pubNub  = PubNub(
            PNConfiguration(uuid = userId!!).apply {
                publishKey = BuildConfig.PUBNUB_PUBLISH_KEY
                subscribeKey = BuildConfig.PUBNUB_SUBSCRIBE_KEY
                logVerbosity = PNLogVerbosity.NONE
            }
        )
    }

    private fun destroyPubNub(){
        pubNub.destroy()
    }



}