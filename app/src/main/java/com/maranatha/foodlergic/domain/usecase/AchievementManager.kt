package com.maranatha.foodlergic.domain.usecase

import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.domain.models.Achievement

class AchievementManager {

    // Hardcoded achievements list
    val achievements = listOf(
        Achievement(
            R.drawable.achievement_welcome_unlocked,
            R.drawable.achievement_welcome_locked,
            "Welcome!",
            "Start your journey by scanning your first food.",
            "welcome",
            1
        ),
        Achievement(
            R.drawable.achievement_10_scans_unlocked,
            R.drawable.achievement_10_scans_locked,
            "10 Food Scans",
            "Scan 10 different foods",
            "scanCount",
            10
        ),
        Achievement(
            R.drawable.achievement_50_scans_unlocked,
            R.drawable.achievement_50_scans_locked,
            "50 Food Scans",
            "Scan 50 different foods",
            "scanCount",
            50
        ),
        Achievement(
            R.drawable.achievement_100_scans_unlocked,
            R.drawable.achievement_100_scans_locked,
            "100 Food Scans",
            "Scan 100 different foods",
            "scanCount",
            100
        ),
        Achievement(
            R.drawable.achievement_1000_scans_unlocked,
            R.drawable.achievement_1000_scans_locked,
            "1000 Food Scans",
            "Scan 1000 different foods",
            "scanCount",
            1000
        ),
        Achievement(
            R.drawable.achievement_10000_scans_unlocked,
            R.drawable.achievement_10000_scans_locked,
            "10000 Food Scans",
            "Scan 10,000 different foods",
            "scanCount",
            10000
        ),
        Achievement(
            R.drawable.achievement_20000_scans_unlocked,
            R.drawable.achievement_20000_scans_locked,
            "20000 Food Scans",
            "Scan 20,000 different foods",
            "scanCount",
            20000
        ),
        Achievement(
            R.drawable.achievement_30000_scans_unlocked,
            R.drawable.achievement_30000_scans_locked,
            "30000 Food Scans",
            "Scan 30,000 different foods",
            "scanCount",
            30000
        ),
        Achievement(
            R.drawable.achievement_40000_scans_unlocked,
            R.drawable.achievement_40000_scans_locked,
            "40000 Food Scans",
            "Scan 40,000 different foods",
            "scanCount",
            40000
        ),
        Achievement(
            R.drawable.achievement_50000_scans_unlocked,
            R.drawable.achievement_50000_scans_locked,
            "50000 Food Scans",
            "Scan 50,000 different foods",
            "scanCount",
            50000
        ),
        Achievement(
            R.drawable.achievement_60000_scans_unlocked,
            R.drawable.achievement_60000_scans_locked,
            "60000 Food Scans",
            "Scan 60,000 different foods",
            "scanCount",
            60000
        ),
        Achievement(
            R.drawable.achievement_70000_scans_unlocked,
            R.drawable.achievement_70000_scans_locked,
            "70000 Food Scans",
            "Scan 70,000 different foods",
            "scanCount",
            70000
        ),
        Achievement(
            R.drawable.achievement_80000_scans_unlocked,
            R.drawable.achievement_80000_scans_locked,
            "80000 Food Scans",
            "Scan 80,000 different foods",
            "scanCount",
            80000
        ),
        Achievement(
            R.drawable.achievement_90000_scans_unlocked,
            R.drawable.achievement_90000_scans_locked,
            "90000 Food Scans",
            "Scan 90,000 different foods",
            "scanCount",
            90000
        ),
        Achievement(
            R.drawable.achievement_100000_scans_unlocked,
            R.drawable.achievement_100000_scans_locked,
            "100000 Food Scans",
            "Scan 100,000 different foods",
            "scanCount",
            100000
        ),
        Achievement(
            R.drawable.achievement_1000000_scans_unlocked,
            R.drawable.achievement_1000000_scans_locked,
            "1000000 Food Scans",
            "Scan 1,000,000 different foods",
            "scanCount",
            1000000
        )

        // Add other achievements...
//        Achievement(R.drawable.first_scan,  "First Scan",  "firstScan",1),
    )

    // Function to check if a user has unlocked a specific achievement
    fun checkAchievementUnlocked(achievement: Achievement, scanCount: Int): Boolean {
        return when (achievement.type) {
            "scanCount" -> scanCount >= achievement.threshold
//            "allergenDetection" -> detectedAllergens.contains(achievement.name)
//            "scanStreak" -> /* Check if the user has scanned for X consecutive days */
            else -> false
        }
    }
}