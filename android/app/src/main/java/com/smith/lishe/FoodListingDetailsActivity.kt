package com.smith.lishe

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle
import com.google.android.gms.maps.model.MarkerOptions
import com.smith.lishe.databinding.ActivityFoodListingDetailsBinding
import com.smith.lishe.utils.BitmapHelper
import com.smith.lishe.viewmodel.FoodListingDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FoodListingDetailsActivity : AppCompatActivity() {
    private val viewModel: FoodListingDetailsViewModel by viewModels()

    private lateinit var binding: ActivityFoodListingDetailsBinding
    private var progressBar: ProgressBar? = null
    private var sharedPreferences: SharedPreferences? = null
    private val sharedPrefFile = "com.smith.lishe.user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.foodListingProgressBar
        progressBar!!.visibility = View.VISIBLE

        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)


        viewModel.listing.observe(this, Observer {
            val foodListing = it

            progressBar!!.visibility = View.INVISIBLE

            binding.foodDetailsFoodNameTextView.text = foodListing.title
            binding.foodDetailsDescriptionTextView.text = foodListing.description
            binding.foodDetailsFoodExpirationTextView.text =
                getString(R.string.expiration_date, foodListing.expiration)

            val imgUri = foodListing.imageUrl.toUri().buildUpon().scheme("https").build()
            binding.foodDetailsImage.load(imgUri) {
                crossfade(true)
                placeholder(R.drawable.ic_loading)
                error(R.drawable.ic_broken_image)
            }

            viewModel.userDetails.observe(this, Observer {
                val listingUser = it
                binding.foodDetailsOwnerNameTextView.text = "${listingUser.firstName} ${listingUser.lastName}"
                binding.foodDetailsOwnerRatingTextView.text = getString(R.string.user_rating)

                if (foodListing.status != "available") {
                    binding.foodDetailsCallButton.text = listingUser.phone.toString()
                }

                val imgUri = listingUser.imageUrl.toUri().buildUpon().scheme("https").build()
                binding.foodDetailsOwnerImage.load(imgUri) {
                    crossfade(true)
                    placeholder(R.drawable.ic_loading)
                    error(R.drawable.ic_broken_image)
                }

                binding.foodDetailsCallButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:${listingUser.phone}")
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
                progressBar!!.visibility = View.INVISIBLE
            })

            val mapFragment = supportFragmentManager.findFragmentById(
                R.id.food_details_pickup_map_fragment
            ) as? SupportMapFragment
            mapFragment?.getMapAsync { googleMap ->
                addPickupMarker(googleMap, foodListing.latitude, foodListing.longitude)

            }
        })
    }

    private fun addPickupMarker(googleMap: GoogleMap, lat: Double, lng: Double) {
        val userActivity =  sharedPreferences!!.getString(LoginActivity.USER_TYPE, "collector")
        val foodPickupLocation = LatLng(lat, lng)
        val markerIconResource = when (userActivity) {
            "lister" -> R.drawable.ic_food
            else -> R.drawable.ic_collector_marker
        }
        val markerIcon: BitmapDescriptor by lazy {
            val color = ContextCompat.getColor(this, R.color.green_dark)
            BitmapHelper.vectorToBitmap(this, markerIconResource, color)
        }

        googleMap.addMarker(
            MarkerOptions()
                .icon(markerIcon)
                .position(foodPickupLocation)
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(foodPickupLocation))
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        //Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);

    }
}