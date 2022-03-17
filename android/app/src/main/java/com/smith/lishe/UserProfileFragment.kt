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
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import coil.load
import com.smith.lishe.databinding.FragmentUserProfileBinding
import com.smith.lishe.viewmodel.UserProfileViewModel

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {
    private val viewModel: UserProfileViewModel by viewModels()
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

        viewModel.user.observe(viewLifecycleOwner, Observer {
            val userDetails = it

            if (userDetails.userType == "collector") {
                binding.userProfileCreateListingButton.isEnabled = false
            }

            val imgUri = userDetails.imageUrl.toUri().buildUpon().scheme("https").build()
            binding.profilePictureImage.load(imgUri) {
                crossfade(true)
                placeholder(R.drawable.ic_loading)
                error(R.drawable.ic_broken_image)
            }

            binding.userProfileNameTextView.text = getString(R.string.user_name, userDetails.firstName, userDetails.lastName)
            binding.userProfilePhoneTextView.text = getString(R.string.user_phone_number, userDetails.phone.toBigDecimal())
            binding.userRatingTextView.text = getString(R.string.rating_value, userDetails.userRating)
            binding.userProfileEmailTextView.text = userDetails.email

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}