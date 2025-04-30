package com.maranatha.foodlergic.presentation.achievement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.maranatha.foodlergic.databinding.FragmentAchievementBinding
import com.maranatha.foodlergic.domain.usecase.AchievementManager
import com.maranatha.foodlergic.presentation.viewmodel.UserProfileViewModel
import com.maranatha.foodlergic.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AchievementFragment : Fragment() {
    private var _binding: FragmentAchievementBinding? = null
    private val binding get() = _binding!!

    private lateinit var achievementAdapter: AchievementAdapter
    private val profileViewModel: UserProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.getAchievement()

        binding.allergyRecyclerView.layoutManager = GridLayoutManager(context, 3)
        achievementAdapter = AchievementAdapter()
        binding.allergyRecyclerView.adapter = achievementAdapter

        observeUserAchievement()
    }

    private fun observeUserAchievement() {
        profileViewModel.userAchievements.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                }

                is Resource.Success -> {

                    val achievementManager = AchievementManager()
                    val unlockedAchievements = result.data ?: emptyList()

                    val userAchievements = achievementManager.achievements.map { ach ->
                        val unlocked = unlockedAchievements.any { it.name == ach.name }
                        ach.copy(isUnlocked = unlocked)
                    }
                    achievementAdapter.submitList(userAchievements)
                }

                is Resource.Error -> {
                    Log.d("rezon-dbg", "error: ${result.message}")
                    // Show error message
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}