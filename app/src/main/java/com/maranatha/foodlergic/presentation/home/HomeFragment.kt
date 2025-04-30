package com.maranatha.foodlergic.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.maranatha.foodlergic.databinding.FragmentHomeBinding
import com.maranatha.foodlergic.domain.models.LevelInfo
import com.maranatha.foodlergic.presentation.profile.FoodScanHistoryAdapter
import com.maranatha.foodlergic.presentation.viewmodel.UserProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodScanHistoryAdapter: FoodScanHistoryAdapter

    private val profileViewModel: UserProfileViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getUserData()
        profileViewModel.fetchRecentPredictions()
        foodScanHistoryAdapter = FoodScanHistoryAdapter()
        binding.rvFoodScanHistory.adapter = foodScanHistoryAdapter
        binding.rvFoodScanHistory.layoutManager = LinearLayoutManager(context)
        loadUserProfile()
        loadFoodScanHistory()
        // Navigasi atau tindakan klik tombol
        binding.iconScanMyFood.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPredictFragment())
        }

        binding.iconProfile.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUserProfileFragment())
        }

        binding.iconLeaderboard.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLeaderboardFragment())
        }

        binding.redeemRewardIcon.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRewardFragment())
        }

        binding.foodScanViewAll.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToHistoryFoodScanFragment())
        }
    }

    private fun loadUserProfile() {
        profileViewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.helloText.text = "Hello, ${user.username}"
            val scanCount = user.scanCount
            val levelInfo = getLevelInfo(scanCount)
            val progress = getLevelProgress(scanCount)
            binding.levelProgressBar.max = 100
            binding.levelProgressBar.progress = (progress * 100).toInt()

            binding.rewardProgress.text = "$scanCount / ${levelInfo.maxScan} Points"
            binding.tvLevelTitle.text = levelInfo.name
        }
    }

    private fun loadFoodScanHistory() {
        profileViewModel.recentPredictions.observe(viewLifecycleOwner) { listFoodScan ->
            Log.d("rezon-dbg", "loadFoodScanHistory: ${listFoodScan.toString()}")
            foodScanHistoryAdapter.submitList(listFoodScan)
        }
    }

    fun getLevelInfo(scanCount: Int): LevelInfo {
        return when {
            scanCount < 100 -> LevelInfo("Rookie", 0, 100)
            scanCount < 1000 -> LevelInfo("Beginner", 100, 1000)
            scanCount < 30000 -> LevelInfo("Explorer", 1000, 30000)
            scanCount < 60000 -> LevelInfo("Expert", 30000, 60000)
            else -> LevelInfo("Master Scanner", 60000, 90000) // progress stop di Master
        }
    }

    fun getLevelProgress(scanCount: Int): Float {
        val levelInfo = getLevelInfo(scanCount)
        val clamped = (scanCount - levelInfo.minScan).coerceAtLeast(0)
        val total = (levelInfo.maxScan - levelInfo.minScan).toFloat()
        return (clamped / total).coerceIn(0f, 1f)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
