package com.smith.lishe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smith.lishe.databinding.ActivityRatingBinding

class RatingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRatingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}