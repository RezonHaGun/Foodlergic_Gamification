package com.maranatha.foodlergic.presentation.achievement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.maranatha.foodlergic.databinding.ActivityAchievementsBinding
import com.maranatha.foodlergic.domain.models.Achievement
import com.maranatha.foodlergic.domain.usecase.AchievementManager


class AchievementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAchievementsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menggunakan ViewBinding untuk layout
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val achievements = getListAchievement()
        // Mengatur RecyclerView dengan GridLayoutManager
        binding.allergyRecyclerView.layoutManager = GridLayoutManager(this, 3) // 3 kolom
        val adapter = AchievementAdapter()
        adapter.submitList(achievements)
        binding.allergyRecyclerView.adapter = adapter
    }


    private fun getListAchievement(): List<Achievement> {
        val achievementManager = AchievementManager()
        return achievementManager.achievements
    }
}
