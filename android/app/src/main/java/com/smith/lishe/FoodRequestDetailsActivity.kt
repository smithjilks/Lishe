package com.smith.lishe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.smith.lishe.databinding.ActivityFoodListingDetailsBinding
import com.smith.lishe.databinding.ActivityFoodRequestDetailsBinding
import com.smith.lishe.model.UpdateRequestModel
import com.smith.lishe.utils.BitmapHelper
import com.smith.lishe.viewmodel.FoodListingDetailsViewModel
import com.smith.lishe.viewmodel.RequestDetailsViewModel
import com.smith.lishe.viewmodel.RequestsViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.RequestBody

@AndroidEntryPoint
class FoodRequestDetailsActivity : AppCompatActivity() {
    private val viewModel: RequestDetailsViewModel by viewModels()
    private lateinit var binding: ActivityFoodRequestDetailsBinding
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sharedPreferences: SharedPreferences? = null
        val sharedPrefFile = "com.smith.lishe.user"

        binding = ActivityFoodRequestDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = application.applicationContext.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )

        val userType = sharedPreferences.getString(LoginActivity.USER_TYPE, "")
        Log.d("User Type Food req Acrivity", userType.toString())

        progressBar = binding.foodRequestProgressBar
        progressBar!!.visibility = View.VISIBLE


        binding.requestRequestLogPickupButton.isVisible = false
        binding.cancelRequestButton.isVisible = false

        if (userType == "lister") {
            binding.requestRequestLogPickupButton.isVisible = true
            binding.cancelRequestButton.isVisible = true
        }

        viewModel.request.observe(this, Observer {
            val foodRequest = it
            val requestListingDetails = it.listingDetails[0]

            binding.foodRequestTitleTextView.text = requestListingDetails.title

            if (foodRequest.status == "pending") {
                binding.requestRequestLogPickupButton.text = "Accept Request"
            }

            viewModel.userDetails.observe(this, Observer {
                val user = it
                binding.foodRequestUserNameTextView.text = "${user.firstName} ${user.lastName}"

                val imgUri: Uri = user.imageUrl.toUri().buildUpon().scheme("https").build()
                binding.foodRequestUserImage.load(imgUri) {
                    crossfade(true)
                    placeholder(R.drawable.ic_loading)
                    error(R.drawable.ic_broken_image)
                }


                binding.foodRequestCallButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:${user.phone}")
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
                progressBar!!.visibility = View.INVISIBLE
            })

            binding.requestRequestLogPickupButton.setOnClickListener {
                val statusRequestBody = when (foodRequest.status) {
                    "pending" -> UpdateRequestModel("confirmed")
                    "confirmed" -> UpdateRequestModel("collected")
                    else -> UpdateRequestModel(foodRequest.status)
                }

                viewModel.updateRequest(statusRequestBody)
            }

            binding.cancelRequestButton.setOnClickListener {
                if (foodRequest.status != "confirmed") {
                    val statusReqBody = UpdateRequestModel("cancelled")
                    viewModel.updateRequest(statusReqBody)
                } else {
                    Toast.makeText(
                        this,
                        "You can't cancel a confirmed request", Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

            val mapFragment = supportFragmentManager.findFragmentById(
                R.id.food_request_map_fragment
            ) as? SupportMapFragment
            mapFragment?.getMapAsync { googleMap ->
                addUserMarkers(
                    googleMap,
                    requestListingDetails.latitude,
                    requestListingDetails.longitude
                )

            }

        })

        viewModel.requestConfirmed.observe(this, Observer {
            var toastMessage = "Failed"
            if (it) {
                toastMessage = "Success"
                onBackPressed()
            }
            Toast.makeText(this, "Operation $toastMessage", Toast.LENGTH_LONG).show()
        })

        viewModel.requestCollected.observe(this, Observer {
            var toastMessage = "Failed"
            if (it) {
                toastMessage = "Success"
                onBackPressed()
            }
            Toast.makeText(this, "Operation $toastMessage", Toast.LENGTH_LONG).show()
        })

        viewModel.requestCancelled.observe(this, Observer {
            var toastMessage = "Failed"
            if (it) {
                toastMessage = "Success"
                onBackPressed()
            }
            Toast.makeText(this, "Operation $toastMessage", Toast.LENGTH_LONG).show()
        })


    }

    private fun addUserMarkers(googleMap: GoogleMap, lat: Double, lng: Double) {
        val foodPickupLocation = LatLng(lat, lng)
        val pickupIconResource = R.drawable.ic_food

        val pickupMarkerIcon: BitmapDescriptor by lazy {
            val color = ContextCompat.getColor(this, R.color.green_dark)
            BitmapHelper.vectorToBitmap(this, pickupIconResource, color)
        }

        googleMap.addMarker(
            MarkerOptions()
                .icon(pickupMarkerIcon)
                .position(foodPickupLocation)
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(foodPickupLocation))
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        //Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);

    }

}