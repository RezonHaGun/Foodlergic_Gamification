package com.maranatha.foodlergic.presentation.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.databinding.FragmentUserProfileBinding
import com.maranatha.foodlergic.presentation.viewmodel.AllergyViewModel
import com.maranatha.foodlergic.presentation.viewmodel.UserProfileViewModel
import com.maranatha.foodlergic.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllergyViewModel by viewModels()
    private val profileViewModel: UserProfileViewModel by viewModels()

    private lateinit var achievementAdapter: UserProfileAchievementAdapter
    private lateinit var allergyAdapter: UserProfileAllergicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()

        profileViewModel.getUserData()
        profileViewModel.getAchievement()
        viewModel.getUserAllergies()

        achievementAdapter = UserProfileAchievementAdapter()
        allergyAdapter = UserProfileAllergicAdapter()
        binding.rvAchievments.layoutManager = GridLayoutManager(context, 3)
        binding.rvAchievments.adapter = achievementAdapter
        binding.selectedAllergiesRecyclerView.layoutManager = GridLayoutManager(context, 4)
        binding.selectedAllergiesRecyclerView.adapter = allergyAdapter
        observeUserAchievement()
        observeGetAllergiesFromAPI()
        observeCLearUserSession()

        binding.tvAchievementEditText.setOnClickListener {
            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToAchievementFragment())
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user_profile_menu, menu) // Inflating menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                // Action logout, misalnya:
                profileViewModel.clearSession()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun observeGetAllergiesFromAPI() {
        viewModel.userAllergies.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                }

                is Resource.Success -> {
                    // Extract allergy names into a list

                    allergyAdapter.submitList(result.data)
                }

                is Resource.Error -> {
                    // Show error message
                    Log.d("rezon-dbg", "error: ${result.message}")
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observeCLearUserSession() {
        profileViewModel.clearUserStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                }

                is Resource.Success -> {
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()

                    findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToLoginFragment())
                }

                is Resource.Error -> {
                    // Show error message
                    Log.d("rezon-dbg", "error: ${result.message}")
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observeUserAchievement() {
        profileViewModel.userAchievements.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                }

                is Resource.Success -> {
                    achievementAdapter.submitList(result.data)
                }

                is Resource.Error -> {
                    Log.d("rezon-dbg", "error: ${result.message}")
                    // Show error message
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadUserProfile() {
        profileViewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.usernameText.text = user.username
            binding.profileImage.setImageResource(R.drawable.ic_baseline_account_circle_24)
            binding.tvLevelTitle.text = user.level
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}