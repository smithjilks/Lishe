package com.smith.lishe

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.smith.lishe.BuildConfig.GOOGLE_MAPS_API_KEY
import com.smith.lishe.data.foodlisting.datasource.ListingRemoteDataSource
import com.smith.lishe.data.requests.datasource.RequestsRemoteDataSource
import com.smith.lishe.databinding.ActivityCreateListingBinding
import com.smith.lishe.network.ListingApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class CreateListingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateListingBinding
    private val REQUEST_CODE = 2
    private val AUTOCOMPLETE_REQUEST_CODE = 3

    private var sharedPreferences: SharedPreferences? = null
    private val sharedPrefFile = "com.smith.lishe.user"

    private var foodImageUri: Uri? = null
    private var progressBar: ProgressBar? = null

    private var locationLatLng: LatLng? = null
    private val TAG = "CreateListingActivity"


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCreateListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
        val userType =  sharedPreferences!!.getString(LoginActivity.USER_TYPE, "individual")

        progressBar = binding.createListingProgressBar
        progressBar!!.visibility = View.GONE

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        binding.createListingButton.setOnClickListener {
            if (isValidUserInput()) {
                progressBar!!.visibility = View.VISIBLE
                binding.createListingButton.isEnabled = false

                val foodTitle = RequestBody.create( MediaType.parse("multipart/form-data"), binding.createListingTitleEditText.text.toString())
                val foodDescription = RequestBody.create( MediaType.parse("multipart/form-data"),binding.createListingDescriptionEditText.text.toString())
                val expirationDate = RequestBody.create( MediaType.parse("multipart/form-data"), binding.createListingExpirationDateTextView.text.toString())
                val latitude = RequestBody.create( MediaType.parse("multipart/form-data"), locationLatLng!!.latitude.toString())
                val longitude = RequestBody.create( MediaType.parse("multipart/form-data"),locationLatLng!!.longitude.toString())
                val individual = RequestBody.create( MediaType.parse("multipart/form-data"),
                    when(userType) {
                    "individual" -> true
                    else -> false
                    }.toString()
                )

                val imageRequestBody = foodImageUri?.let { it -> getImageRequestBody(it) }

                GlobalScope.launch {
                    if (imageRequestBody != null) {
                        createListing(
                            foodTitle,
                            foodDescription,
                            expirationDate,
                            latitude,
                            longitude,
                            individual,
                            imageRequestBody
                        )
                    }

                }
            }
        }

        binding.createListingSelectImageButton.setOnClickListener { openGalleryForImage() }

        binding.createListingSelectLocationButton.setOnClickListener { getLocation() }

        binding.createListingSelectExpirationDateButton.setOnClickListener {
            datePicker.addOnPositiveButtonClickListener {
                val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
                val date = LocalDate.parse(datePicker.headerText.toString(), formatter)
                binding.createListingExpirationDateTextView.text = date.toString()
            }
            datePicker.show(supportFragmentManager, "tag")
        }

    }

    private suspend fun createListing(
        foodTitle: RequestBody,
        foodDescription: RequestBody,
        expirationDate: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody,
        individual: RequestBody,
        imageFile: MultipartBody.Part
    ) {

        try {
            Log.d("Check Request Body", foodTitle.toString())
            val response = ListingRemoteDataSource(ListingApi, Dispatchers.IO).createNewListing(
                "Bearer ${sharedPreferences!!.getString(LoginActivity.USER_TOKEN, "")}",
                foodTitle,
                foodDescription,
                latitude,
                longitude,
                expirationDate,
                individual,
                imageFile)

            runOnUiThread {
                onBackPressed()
                Toast.makeText(
                    this,
                    "Create Listing Successful",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            Log.e("Create Listings Error", e.toString())
            runOnUiThread {
                progressBar!!.visibility = View.INVISIBLE
                binding.createListingButton.isEnabled = true
                Toast.makeText(
                    this,
                    "Create Listing Failed. Try again",
                    Toast.LENGTH_LONG
                ).show()

            }

        }

    }

    private fun isValidUserInput(): Boolean {
        val foodTitle = binding.createListingTitleEditText
        val foodDescription = binding.createListingDescriptionEditText
        val expirationDate = binding.createListingExpirationDateTextView
        val locationTextView = binding.createListingLocationTextView


        return if (foodTitle.text.isNullOrEmpty()
            || foodDescription.text.isNullOrEmpty()
            || expirationDate.text.isNullOrEmpty()
            || locationTextView.text.isNullOrEmpty()
            || foodImageUri == null
        ) {
            foodDescription.error = getString(R.string.error_required)
            foodTitle.error = getString(R.string.error_required)
            expirationDate.error = getString(R.string.error_required)
            locationTextView.error = getString(R.string.error_required)
            false
        } else if (foodDescription.text.toString().length < 20) {
            foodDescription.error = getString(R.string.error_longer_than_20)
            return false
        } else if (foodTitle.text.toString().length < 10) {
            foodTitle.error = getString(R.string.error_longer_than_10)
            return false
        } else {
            // Clear the error.
            foodTitle.error = null
            foodDescription.error = null
            expirationDate.error = null
            locationTextView.error = null
            true
        }

    }

    private fun getImageRequestBody(uri: Uri): MultipartBody.Part {
        val file = File(getRealPathFromURI(uri))
        Log.d("File path", file.absolutePath)
        val requestFile = RequestBody.create(
            MediaType.parse(contentResolver.getType(uri)), file
        )
        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }

    private fun openGalleryForImage() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            foodImageUri = data?.data
            binding.foodDetailsImage.setImageURI(foodImageUri)
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        binding.createListingLocationTextView.text = place.name
                        locationLatLng = place.latLng
                        Log.i(TAG, "Place: ${place.name}, ${place.id}")
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage!!)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)


    }

    private fun getRealPathFromURI(uri: Uri?): String {
        var path = ""
        if (contentResolver != null) {
            val cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    private fun getLocation() {
        /**
         * Initialize Places. API Key is located in
         * com.smith.lishe.BuildConfig.GOOGLE_MAPS_API_KEY which is ignored by version control
         */
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, GOOGLE_MAPS_API_KEY)
        }

        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

    }

}