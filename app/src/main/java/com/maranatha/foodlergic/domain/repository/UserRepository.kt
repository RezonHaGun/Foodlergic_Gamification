package com.maranatha.foodlergic.domain.repository

import com.maranatha.foodlergic.domain.models.Predict
import com.maranatha.foodlergic.domain.models.User

interface UserRepository {
    suspend fun getUserAchievements(userId: String): Map<String, Boolean>

    suspend fun updateUserAchievements(
        userId: String,
        unlocked: Map<String, Boolean>
    )

    suspend fun updateScanCount(userId: String, newScanCount: Int)

    suspend fun getScanCount(userId: String): Int

    suspend fun getCurrentUser(): User
    suspend fun getRecentPredictions(userId: String): List<Predict>
    suspend fun clearUser()

}