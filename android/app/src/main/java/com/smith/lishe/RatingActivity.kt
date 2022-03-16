package com.smith.lishe

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.smith.lishe.data.foodlisting.datasource.ListingRemoteDataSource
import com.smith.lishe.data.reviews.datasource.ReviewsRemoteDataSource
import com.smith.lishe.databinding.ActivityRatingBinding
import com.smith.lishe.model.ReviewDetailsModel
import com.smith.lishe.network.ListingApi
import com.smith.lishe.network.ReviewApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RatingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRatingBinding
    private var progressBar: ProgressBar? = null

    private var sharedPreferences: SharedPreferences? = null
    private val sharedPrefFile = "com.smith.lishe.user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
        val token =  sharedPreferences!!.getString(LoginActivity.USER_TOKEN, "")


        progressBar = binding.rateListingProgressBar
        progressBar!!.visibility = View.GONE
        
        binding.rateListingButton.setOnClickListener {
            progressBar!!.visibility = View.VISIBLE

            val ownerId = intent.getStringExtra(MainActivity.LISTING_USER_ID)
            val requestId = intent.getStringExtra(MainActivity.REQUEST_ID)
            val ratingDetails = ReviewDetailsModel(
                ownerId!!,
                requestId!!,
                binding.rateListingRatingBar.rating,
                binding.rateListingDescriptionEditText.text.toString()
            )
            GlobalScope.launch {
                submitReview(token!!, ratingDetails)
            }
        }
    }

    private suspend fun submitReview(token: String, ratingDetails: ReviewDetailsModel) {
        try {
            val response = ReviewsRemoteDataSource(ReviewApi, Dispatchers.IO).createNewReview("Bearer $token", ratingDetails)

            runOnUiThread {
                onBackPressed()
                Toast.makeText(
                    this,
                    "Review Submitted Successfully",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            Log.e("Review Activity", e.toString())
            runOnUiThread {
                progressBar!!.visibility = View.INVISIBLE
                binding.rateListingButton.isEnabled = true
                Toast.makeText(
                    this,
                    "Could not submit Review.",
                    Toast.LENGTH_LONG
                ).show()

            }

        }

    }


}