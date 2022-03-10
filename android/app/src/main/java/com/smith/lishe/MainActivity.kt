package com.smith.lishe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.smith.lishe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val LISTING_ID = "listingId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        supportFragmentManager.beginTransaction().apply {
            replace(binding.navHostFragment.id, HomeFragment())
            addToBackStack(null)
            commit()
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            var selectedFragment: Fragment? = null
            when (it.itemId) {
                R.id.menu_home -> {
                    selectedFragment = HomeFragment()
                }
                R.id.menu_history -> {
                    selectedFragment = HistoryFragment()
                }
                R.id.menu_profile -> {
                    selectedFragment = UserProfileFragment()
                }
            }
            if (selectedFragment != null) {
                changeFragment(selectedFragment)
            };

            return@setOnItemSelectedListener true
        }

    }
    private fun changeFragment(fragmentToChange: Fragment): Unit {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.navHostFragment.id, fragmentToChange)
            addToBackStack(null)
            commit()
        }
    }
}
