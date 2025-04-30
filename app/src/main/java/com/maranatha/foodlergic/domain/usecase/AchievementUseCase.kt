package com.maranatha.foodlergic.domain.usecase

import com.maranatha.foodlergic.domain.models.Achievement
import com.maranatha.foodlergic.domain.repository.UserRepository
import javax.inject.Inject

class AchievementUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun checkAchievements(userId: String): List<Achievement> {
        val userAchievements = userRepository.getUserAchievements(userId)
        val scanCount = userRepository.getScanCount(userId)
        val unlockedMap = userAchievements as? Map<String, Boolean> ?: mapOf()

        val achievementManager = AchievementManager()
        val newlyUnlocked = achievementManager.achievements.filter {
            it.type == "scanCount" &&
                    scanCount >= it.threshold &&
                    unlockedMap[it.name] != true
        }

        val updatedUnlocked = unlockedMap.toMutableMap()
        newlyUnlocked.forEach { updatedUnlocked[it.name] = true }

        userRepository.updateUserAchievements(userId, updatedUnlocked)

        return newlyUnlocked
    }
}
