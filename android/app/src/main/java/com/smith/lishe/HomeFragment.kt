package com.smith.lishe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.size
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smith.lishe.adapter.ListingAdapter
import com.smith.lishe.databinding.FragmentHomeBinding
import com.smith.lishe.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private var progressBar: ProgressBar? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBar = binding.homeProgressBar
        progressBar!!.visibility = View.VISIBLE
        recyclerView = binding.homeRecyclerView
        // Sets the LayoutManager of the recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.listings.observe(viewLifecycleOwner, Observer {
            recyclerView.adapter = context?.let { it1 -> ListingAdapter(it1, it) }
            progressBar!!.visibility = View.INVISIBLE
            if (recyclerView.size == 0) {
                Toast.makeText(context, "You have no listings", Toast.LENGTH_LONG).show()
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}