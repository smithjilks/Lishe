package com.smith.lishe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.smith.lishe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                    .add<HomeFragment>(R.id.nav_host_fragment)
            }
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
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment)
                    .commit()
            };

            return@setOnItemSelectedListener true
        }

    }
}
