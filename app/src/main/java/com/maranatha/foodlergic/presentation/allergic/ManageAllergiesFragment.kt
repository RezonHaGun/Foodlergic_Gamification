package com.maranatha.foodlergic.presentation.allergic

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.data.Preference
import com.maranatha.foodlergic.data.models.Allergy
import com.maranatha.foodlergic.databinding.FragmentManageAllergiesBinding
import com.maranatha.foodlergic.domain.usecase.AchievementManager
import com.maranatha.foodlergic.presentation.achievement.SingleAchievementDialogFragment
import com.maranatha.foodlergic.presentation.viewmodel.AllergyViewModel
import com.maranatha.foodlergic.presentation.viewmodel.UserProfileViewModel
import com.maranatha.foodlergic.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ManageAllergiesFragment : Fragment() {
    private var _binding: FragmentManageAllergiesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferences: Preference
    private val viewModel: AllergyViewModel by viewModels()

    private val profileViewModel: UserProfileViewModel by viewModels()
    private val manageAllergiesFragmentArgs: ManageAllergiesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageAllergiesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allergyListAdapter = AllergyAdapter()
        binding.allergyRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.allergyRecyclerView.adapter = allergyListAdapter

        allergyListAdapter.submitList(getAllergyList())
        binding.submitAllergiesBtn.setOnClickListener {
            val selectedAllergies = allergyListAdapter.currentList.filter { it.isSelected }
            Log.d("rezon-dbg", "selectedAllergies: $selectedAllergies")

            val allergyNames = selectedAllergies.map { it.name.lowercase() }
            if (manageAllergiesFragmentArgs.isAnonymous) {
                preferences.saveAllergies(allergyNames)
                Toast.makeText(context, "Allergies saved locally.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(ManageAllergiesFragmentDirections.actionManageAllergiesFragmentToPredictFragment(true))
            } else {
                viewModel.submitAllergies(allergyNames)
            }
        }
        observeSubmitAllergies()
        observeSetAchievement()
    }

    private fun observeSubmitAllergies() {
        viewModel.submitResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                }

                is Resource.Success -> {
                    val achievementManager = AchievementManager()
                    val welcomeAchievement =
                        achievementManager.achievements.first { it.type == "welcome" }

                    profileViewModel.setAchievement(welcomeAchievement)
                }

                is Resource.Error -> {
                    Log.d("rezon-dbg", "error: ${result.message}")
                    // Show error message
//                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observeSetAchievement() {
        profileViewModel.setAchievement.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Show loading state
                }

                is Resource.Success -> {

                    val achievementManager = AchievementManager()
                    val welcomeAchievement =
                        achievementManager.achievements.first { it.type == "welcome" }
                    SingleAchievementDialogFragment(
                        welcomeAchievement
                    ) {
                        findNavController().navigate(ManageAllergiesFragmentDirections.actionManageAllergiesFragmentToHomeFragment())
                    }.show(childFragmentManager, "SingleAchievement")

                }

                is Resource.Error -> {
                    Log.d("rezon-dbg", "error: ${result.message}")
                    // Show error message
//                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getAllergyList(): List<Allergy> {
        return listOf(
            Allergy(name = "Ikan", image = R.drawable.fish_image),
            Allergy(name = "Kerang", image = R.drawable.shellfish_image),
            Allergy(name = "Kepiting", image = R.drawable.crab_image),
            Allergy(name = "Udang", image = R.drawable.shrimp_image),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}