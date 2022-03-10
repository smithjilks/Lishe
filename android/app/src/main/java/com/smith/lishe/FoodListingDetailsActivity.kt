package com.smith.lishe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.smith.lishe.databinding.ActivityFoodListingDetailsBinding
import com.smith.lishe.databinding.ActivityMainBinding

class FoodListingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodListingDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}