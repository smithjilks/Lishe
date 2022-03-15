package com.smith.lishe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smith.lishe.adapter.RequestsAdapter
import com.smith.lishe.databinding.FragmentRequestsBinding
import com.smith.lishe.viewmodel.RequestsViewModel

class RequestsFragment : Fragment(R.layout.fragment_requests) {
    private val viewModel: RequestsViewModel by viewModels()

    private var _binding: FragmentRequestsBinding? = null
    private var progressBar: ProgressBar? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBar = binding.requestsProgressBar
        progressBar!!.visibility = View.VISIBLE
        recyclerView = binding.requestsRecyclerView
        // Sets the LayoutManager of the recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.requests.observe(viewLifecycleOwner, Observer {
            Log.d("Requests ViewModel", it.toString())
            recyclerView.adapter = context?.let { it1 ->
                RequestsAdapter(it1, it)
            }

            if (recyclerView.adapter?.itemCount == 0) {
                Toast.makeText(context, "You have no request. Create listings to get requests", Toast.LENGTH_LONG).show()
            }
            progressBar!!.visibility = View.INVISIBLE
        })

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}