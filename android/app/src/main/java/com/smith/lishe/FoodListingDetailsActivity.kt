package com.smith.lishe

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.smith.lishe.databinding.ActivityFoodListingDetailsBinding
import com.smith.lishe.viewmodel.FoodListingDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodListingDetailsActivity : AppCompatActivity() {
    private val viewModel: FoodListingDetailsViewModel by viewModels()

    private lateinit var binding: ActivityFoodListingDetailsBinding
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.foodListingProgressBar
        progressBar!!.visibility = View.VISIBLE

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

            val mapFragment = supportFragmentManager.findFragmentById(
                R.id.food_details_pickup_map_fragment
            ) as? SupportMapFragment
            mapFragment?.getMapAsync { googleMap ->
                addPickupMarker(googleMap, foodListing.latitude, foodListing.longitude)
            }


            viewModel.userDetails.observe(this, Observer {
                val listingUser = it
                binding.foodDetailsOwnerNameTextView.text = "${listingUser.firstName} ${listingUser.lastName}"
                binding.foodDetailsOwnerRatingTextView.text = getString(R.string.user_rating)

                if (foodListing.status != "available"){
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
        })





    }
    private fun addPickupMarker(googleMap: GoogleMap, lat: Double, lng: Double) {
        val foodPickupLocation = LatLng(lat, lng)
        googleMap.addMarker(
            MarkerOptions()
                .title("Your Food awaits")
                .position(foodPickupLocation)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(foodPickupLocation))

    }
}