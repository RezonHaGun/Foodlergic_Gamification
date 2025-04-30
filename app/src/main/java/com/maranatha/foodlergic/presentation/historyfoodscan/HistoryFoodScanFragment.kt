
package com.maranatha.foodlergic.presentation.historyfoodscan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.databinding.FragmentHistoryFoodScanBinding
import com.maranatha.foodlergic.databinding.FragmentHomeBinding
import com.maranatha.foodlergic.presentation.profile.FoodScanHistoryAdapter
import com.maranatha.foodlergic.presentation.viewmodel.UserProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFoodScanFragment : Fragment() {

    private var _binding: FragmentHistoryFoodScanBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: UserProfileViewModel by viewModels()
    private lateinit var foodScanHistoryAdapter: FoodScanHistoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryFoodScanBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.fetchRecentPredictions()
        foodScanHistoryAdapter = FoodScanHistoryAdapter()
        binding.historyFoodScanRecyclerview.adapter = foodScanHistoryAdapter
        binding.historyFoodScanRecyclerview.layoutManager = LinearLayoutManager(context)
        loadFoodScanHistory()
    }
    private fun loadFoodScanHistory() {
        profileViewModel.recentPredictions.observe(viewLifecycleOwner) { listFoodScan ->
            Log.d("rezon-dbg", "loadFoodScanHistory: ${listFoodScan.toString()}")
            foodScanHistoryAdapter.submitList(listFoodScan)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}