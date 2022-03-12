package com.smith.lishe

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smith.lishe.data.users.datasource.RegisterRemoteDataSource
import com.smith.lishe.data.users.repository.RegisterRepository
import com.smith.lishe.databinding.ActivitySignupBinding
import com.smith.lishe.network.UserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val REQUEST_CODE = 1

    private var profilePicUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var isOrganisation = false
        binding.optionIndividual.setOnCheckedChangeListener { compoundButton, b ->
            isOrganisation = !isOrganisation
            binding.signupOrganisationName.isEnabled = isOrganisation
        }

        binding.signupButton.setOnClickListener {
            if (isValidUserInput()) {
                    val firstName = RequestBody.create( MediaType.parse("multipart/form-data"), binding.signupFirstnameEditText.text.toString())
                    val lastName = RequestBody.create( MediaType.parse("multipart/form-data"),binding.signupLastnameEditText.text.toString())
                    val email = RequestBody.create( MediaType.parse("multipart/form-data"), binding.signupEmailEditText.text.toString())
                    val phone = RequestBody.create( MediaType.parse("multipart/form-data"), binding.signupPhoneEditText.text.toString())
                    val password = RequestBody.create( MediaType.parse("multipart/form-data"),binding.signupPasswordEditText.text.toString())
                    val organisation = RequestBody.create( MediaType.parse("multipart/form-data"), when (binding.userOrganisationOptions.checkedRadioButtonId) {
                    R.id.option_organisation -> true
                    else -> false
                    }.toString())

                    val organisationName = RequestBody.create( MediaType.parse("multipart/form-data"), binding.signupOrganisationNameEditText.text.toString())
                    val userType = RequestBody.create( MediaType.parse("multipart/form-data"), when (binding.userActivityOptions.checkedRadioButtonId) {
                        R.id.option_collect -> "collecting"
                        else -> "listing"
                    }.toString())


                val imageRequestBody = profilePicUri?.let { it -> getImageRequestBody(it) }
                GlobalScope.launch {
                    if (imageRequestBody != null) {
                        registerUser(
                            firstName,
                            lastName,
                            email,
                            phone,
                            password,
                            organisation,
                            organisationName,
                            userType,
                            imageRequestBody)
                    }

                }
            }
        }

        binding.profileImage.setOnClickListener { openGalleryForImage() }

        binding.signupHaveAccountButton.setOnClickListener { navToLoginActivity() }

    }

    private suspend fun registerUser(
        firstName: RequestBody,
        lastName: RequestBody,
        email: RequestBody,
        phone: RequestBody,
        password: RequestBody,
        organisation: RequestBody,
        organisationName: RequestBody,
        userType: RequestBody,
        imageFile: MultipartBody.Part
    ) {
        try {
            val response = RegisterRepository(
                RegisterRemoteDataSource(UserApi, Dispatchers.IO),
                firstName,
                lastName,
                email,
                phone,
                password,
                organisation,
                organisationName,
                userType,
                imageFile
            ).fetchRegisterData()

            runOnUiThread(java.lang.Runnable {
                Toast.makeText(
                    this,
                    "Registration Successful",
                    Toast.LENGTH_LONG
                ).show()
            })

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        } catch (e: Exception) {
            Log.e("Sign Up error", e.toString())
            runOnUiThread(java.lang.Runnable {
                Toast.makeText(
                    this,
                    "Registration Failed",
                    Toast.LENGTH_LONG
                ).show()
            })
        }

    }

    private fun navToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun isValidUserInput(): Boolean {
        val firstName = binding.signupFirstnameEditText
        val lastName = binding.signupLastnameEditText
        val email = binding.signupEmailEditText
        val phone = binding.signupPhoneEditText
        val password = binding.signupPasswordEditText
        val confirmPassword = binding.signupConfirmPasswordEditText
        val organisationName = binding.signupOrganisationNameEditText


        return if (!isPasswordValid(password.text!!)
            || !isValidEmail(email.text!!)
            || firstName.text.isNullOrEmpty()
            || lastName.text.isNullOrEmpty()
            || phone.text.isNullOrEmpty()
            || confirmPassword.text.isNullOrEmpty()
            || profilePicUri == null
        ) {
            password.error = getString(R.string.error_password)
            email.error = getString(R.string.error_email)
            firstName.error = getString(R.string.error_required)
            lastName.error = getString(R.string.error_required)
            phone.error = getString(R.string.error_required)
            confirmPassword.error = getString(R.string.error_required)
            false
        } else if (!password.text.toString().equals(confirmPassword.text.toString(), false)) {
            password.error = getString(R.string.error_passwords_dont_match)
            return false
        } else if (organisationName.text.isNullOrEmpty() && binding.optionOrganisation.isChecked) {
            organisationName.error = getString(R.string.error_required)
            return false
        } else {
            // Clear the error.
            password.error = null
            email.error = null
            confirmPassword.error = null
            phone.error = null
            lastName.error = null
            firstName.error = null
            organisationName.error = null
            true
        }

    }

    private fun isPasswordValid(text: Editable?): Boolean {
        return text != null && text.length >= 8
    }

    private fun isValidEmail(text: Editable): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(text).matches()
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
            profilePicUri = data?.data
            binding.profileImage.setImageURI(profilePicUri)
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
}
