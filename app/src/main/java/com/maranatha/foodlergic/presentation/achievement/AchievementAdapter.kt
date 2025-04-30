package com.maranatha.foodlergic.presentation.achievement

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maranatha.foodlergic.data.models.Allergy
import com.maranatha.foodlergic.databinding.ItemAchievementBinding
import com.maranatha.foodlergic.databinding.ItemAllergyBinding
import com.maranatha.foodlergic.domain.models.Achievement

class AchievementAdapter :
    ListAdapter<Achievement, AchievementAdapter.AchievementViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding =
            ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AchievementViewHolder(private val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(achievement: Achievement) {
            binding.achievementText.text = achievement.name
            if (achievement.isUnlocked) {
                binding.achievementImage.setImageResource(achievement.iconResId)
            } else {
                binding.achievementImage.setImageResource(achievement.lockedIconResId)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Achievement>() {
            override fun areItemsTheSame(
                oldItem: Achievement,
                newItem: Achievement
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: Achievement,
                newItem: Achievement
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
