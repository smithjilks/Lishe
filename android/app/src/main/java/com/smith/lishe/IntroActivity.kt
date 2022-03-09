package com.smith.lishe

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smith.lishe.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private val sharedPrefFile = "com.smith.lishe.user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkAuth())
            navToMainActivity()

        binding.introLoginButton.setOnClickListener { navToLoginActivity() }
        binding.introSignupButton.setOnClickListener { navToSignupActivity() }


    }

    private fun checkAuth(): Boolean {
        val userPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        var token = userPreferences.getString(LoginActivity.USER_TOKEN, "")
        val userId= userPreferences.getString(LoginActivity.USER_ID, "")

        if (token == null || token == "")
            return false
        if (userId == null || userId == "")
            return false

        return true
    }

    private fun navToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun navToSignupActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    private fun navToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}