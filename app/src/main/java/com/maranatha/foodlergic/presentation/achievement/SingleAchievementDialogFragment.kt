package com.maranatha.foodlergic.presentation.achievement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.maranatha.foodlergic.databinding.DialogSingleAchievementBinding
import com.maranatha.foodlergic.domain.models.Achievement
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingleAchievementDialogFragment(
    private val achievement: Achievement,
    private val onDismiss: () -> Unit
) : DialogFragment() {

    private var _binding: DialogSingleAchievementBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSingleAchievementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAchievementTitle.text = achievement.name
        binding.ivAchievmentIcon.setImageResource(achievement.iconResId)
        binding.tvAchievementDescription.text = achievement.description

        binding.btnClose.setOnClickListener {
            dismiss()
            onDismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}