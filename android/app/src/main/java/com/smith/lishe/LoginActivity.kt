package com.smith.lishe

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smith.lishe.data.user.datasource.AuthRemoteDataSource
import com.smith.lishe.data.user.repository.LoginRepository
import com.smith.lishe.databinding.ActivityLoginBinding
import com.smith.lishe.model.UserLoginInfo
import com.smith.lishe.network.UserApi
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.signupButton.setOnClickListener { navToSignupActivity() }
        binding.loginButton.setOnClickListener {
            if (isValidUserInput())
                GlobalScope.launch {
                    authenticateUser(
                        binding.loginEmailEditText.text.toString(),
                        binding.loginPasswordEditText.text.toString()
                    )

                }

        }
    }

    private fun isValidUserInput(): Boolean {
        val password = binding.loginPasswordEditText
        val email = binding.loginEmailEditText

        return if (!isPasswordValid(password.text!!) || !isValidEmail(email.text!!)) {
            password.error = getString(R.string.error_password)
            email.error = getString(R.string.error_email)
            false
        } else {
            // Clear the error.
            password.error = null
            email.error = null
            true
        }

    }

    private suspend fun authenticateUser(email: String, password: String) {
        val userLoginInfo = UserLoginInfo(email, password)

        try {
            val response = LoginRepository(
                AuthRemoteDataSource(UserApi, Dispatchers.IO),
                userLoginInfo
            ).fetchAuthData()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        } catch (e: Exception) {
            runOnUiThread(java.lang.Runnable {
                Toast.makeText(
                    this,
                    "Auth Failed",
                    Toast.LENGTH_SHORT
                ).show()
            })
        }


    }

    private fun isPasswordValid(text: Editable?): Boolean {
        return text != null && text.length >= 8
    }

    private fun isValidEmail(text: Editable): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    private fun navToSignupActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}