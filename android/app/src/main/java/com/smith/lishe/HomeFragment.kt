package com.smith.lishe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smith.lishe.adapter.ListingAdapter
import com.smith.lishe.data.foodlisting.datasource.ListingRemoteDataSource
import com.smith.lishe.data.foodlisting.repository.ListingRepository
import com.smith.lishe.databinding.FragmentHomeBinding
import com.smith.lishe.model.ListingModel
import com.smith.lishe.network.ListingApi
import com.smith.lishe.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home){
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null

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
        recyclerView = binding.homeRecyclerView
        // Sets the LayoutManager of the recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.listings.observe(viewLifecycleOwner, Observer {
            recyclerView.adapter = context?.let { it1 -> ListingAdapter(it1, it) }
            Log.d("Home Activity ViewModel Data", it.toString())
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}