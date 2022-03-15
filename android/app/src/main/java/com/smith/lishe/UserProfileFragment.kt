package com.smith.lishe

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.smith.lishe.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {
    private var _binding: FragmentUserProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var sharedPreferences: SharedPreferences? = null
    private val sharedPrefFile = "com.smith.lishe.user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.editProfileIconButton.setOnClickListener {
            Log.d("User Profile", "Edit profile clicked")
        }

        binding.userProfileCreateListingButton.setOnClickListener {
            val intent = Intent(context, CreateListingActivity::class.java)
            startActivity(intent)
        }

        binding.userProfileLogoutButton.setOnClickListener {
            sharedPreferences = context?.getSharedPreferences(sharedPrefFile, AppCompatActivity.MODE_PRIVATE)
            sharedPreferences!!.edit().clear().commit()
            val intent = Intent(context, LoginActivity::class.java)

            intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}