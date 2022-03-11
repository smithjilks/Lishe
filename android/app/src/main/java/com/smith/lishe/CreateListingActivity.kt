package com.smith.lishe

import android.R.attr
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.smith.lishe.BuildConfig.GOOGLE_MAPS_API_KEY
import com.smith.lishe.data.foodlisting.datasource.ListingRemoteDataSource
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
    private val sharedPrefFile = "com.example.android.hellosharedprefs"

    private var foodImageUri: Uri? = null
    private var progressBar: ProgressBar? = null

    private var locationLatLng: LatLng? = null
    private var userType: String? = ""
    private val TAG = "CreateListingActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        userType =  sharedPreferences!!.getString(LoginActivity.USER_TYPE, "individual");

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
            progressBar = binding.createListingProgressBar
            progressBar!!.visibility = View.VISIBLE

            if (isValidUserInput()) {
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
                            imageRequestBody)
                    }

                }
            }
        }

        binding.createListingSelectImageButton.setOnClickListener { openGalleryForImage() }

        binding.createListingSelectLocationButton.setOnClickListener { openPlacePickerView() }

        binding.createListingSelectExpirationDateButton.setOnClickListener {
            datePicker.addOnPositiveButtonClickListener {
                val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
                val date = LocalDate.parse(datePicker.headerText.toString(), formatter)
                binding.createListingExpirationDateTextView.text = date.toString()
            }
            datePicker.show(supportFragmentManager, "tag");
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
            val response = ListingRemoteDataSource(ListingApi, Dispatchers.IO).createNewListing(
                foodTitle,
                foodDescription,
                expirationDate,
                latitude,
                longitude,
                individual,
                imageFile)

            progressBar!!.visibility = View.INVISIBLE
            runOnUiThread(java.lang.Runnable {
                Toast.makeText(
                    this,
                    "Registration Successful",
                    Toast.LENGTH_LONG
                ).show()
            })

        } catch (e: Exception) {
            Log.e("Create Listing Error", e.toString())
            progressBar!!.visibility = View.INVISIBLE
            runOnUiThread(java.lang.Runnable {
                Toast.makeText(
                    this,
                    "Create Listing Failed. Try again",
                    Toast.LENGTH_LONG
                ).show()
            })

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
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(gallery, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            foodImageUri = data?.data
            binding.foodDetailsImage.setImageURI(foodImageUri)
        }

//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                val place = Autocomplete.getPlaceFromIntent(data)
//                binding.createListingLocationTextView.text = data.toString()
//                locationLatLng = place.latLng
//                Log.i(TAG, "Place: " + place.name + ", " + place.id)
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                // TODO: Handle the error.
//                val status: Status = Autocomplete.getStatusFromIntent(data)
//                status.getStatusMessage()?.let { Log.i(TAG, it) }
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }

        if (requestCode === AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode === RESULT_OK) {
                val place: Place = PlacePicker.getPlace(applicationContext, data)
                val latLngQueriedLocation = place.latLng
                locationLatLng = place.latLng
                binding.createListingLocationTextView.setText(place.name)
            }
        }

    }

    private fun getRealPathFromURI(uri: Uri?): String? {
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

//    private fun getLocation() {
//        /**
//         * Initialize Places. API Key is located in
//         * com.smith.lishe.BuildConfig.GOOGLE_MAPS_API_KEY which is ignored by version control
//         */
//        if (!Places.isInitialized()) {
//            Places.initialize(this, GOOGLE_MAPS_API_KEY);
//        }
//
//        // Set the fields to specify which types of place data to return.
//        // Set the fields to specify which types of place data to return.
//        val fields = listOf(Place.Field.NAME)
//
//        // Start the autocomplete intent.
//        val intent = Autocomplete.IntentBuilder(
//            AutocompleteActivityMode.FULLSCREEN, fields
//        )
//            .build(this)
//        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
//
//    }

    private fun openPlacePickerView() {
        val latLngBounds = LatLngBounds(
            LatLng(-4.47166, 33.97559),
            LatLng(3.93726, 41.85688)
        )
        val builder: PlacePicker.IntentBuilder = PlacePicker.IntentBuilder()
        builder.setLatLngBounds(latLngBounds)
        try {
            startActivityForResult(builder.build(this), AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }

}