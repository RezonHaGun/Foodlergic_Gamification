package com.maranatha.foodlergic.presentation.register

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.data.Preference
import com.maranatha.foodlergic.databinding.FragmentRegisterBinding
import com.maranatha.foodlergic.domain.usecase.AchievementManager
import com.maranatha.foodlergic.presentation.achievement.SingleAchievementDialogFragment
import com.maranatha.foodlergic.presentation.viewmodel.AllergyViewModel
import com.maranatha.foodlergic.presentation.viewmodel.AuthViewModel
import com.maranatha.foodlergic.presentation.viewmodel.UserProfileViewModel
import com.maranatha.foodlergic.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferences: Preference

    private val viewModel: AuthViewModel by viewModels()
    private val args: RegisterFragmentArgs by navArgs()
    private val allergyViewModel: AllergyViewModel by viewModels()
    private val profileViewModel: UserProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeRegister()

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            val gender = when (binding.genderRadioGroup.checkedRadioButtonId) {
                R.id.Male -> "Male"
                R.id.Female -> "Female"
                else -> "" // Default if no gender is selected
            }

            if (!isValidInput(name, email, password)) return@setOnClickListener

            viewModel.register(name, email, password,gender)
        }
        observeSubmitAllergies()
        observeSetAchievement()
    }

    private fun isValidInput(name: String, email: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.nameEditText.error = getString(R.string.error_name_required)
            isValid = false
        }

        if (email.isEmpty()) {
            binding.emailEditText.error = getString(R.string.error_email_required)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        if (password.isEmpty()) {
            binding.passwordEditText.error = getString(R.string.error_password_required)
            isValid = false
        } else if (password.length < 6) {
            binding.passwordEditText.error = getString(R.string.error_password_length)
            isValid = false
        }

        return isValid
    }

    private fun observeSubmitAllergies() {
        allergyViewModel.submitResult.observe(viewLifecycleOwner) { result ->
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
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
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
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToHomeFragment())
                    }.show(childFragmentManager, "SingleAchievement")

                }

                is Resource.Error -> {
                    Log.d("rezon-dbg", "error: ${result.message}")
                    // Show error message
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun observeRegister() {
        viewModel.registrationStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    updateSignupButton(isEnabled = false, text = getString(R.string.loading))
                }

                is Resource.Success -> {
                    if (args.isAnonymous) {
                        val selectedAllergies = preferences.getAllergies()
                        allergyViewModel.submitAllergies(selectedAllergies)
                    } else {
                        updateSignupButton(isEnabled = true, text = getString(R.string.signup))
                        Log.d("rezon-dbg", "userId: ${result.data}")
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToManageAllergiesFragment())
                    }
                }

                is Resource.Error -> {
                    updateSignupButton(isEnabled = true, text = getString(R.string.signup))

                    Log.e("RegisterActivity", "Error: ${result.message}")
                    Toast.makeText(
                        context,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateSignupButton(isEnabled: Boolean, text: String) {
        binding.signupButton.isEnabled = isEnabled
        binding.signupButton.text = text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}