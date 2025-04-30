package com.maranatha.foodlergic.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.maranatha.foodlergic.data.Preference
import com.maranatha.foodlergic.domain.models.Predict
import com.maranatha.foodlergic.domain.models.User
import com.maranatha.foodlergic.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sharedPreference: Preference
) : UserRepository {
    override suspend fun getUserAchievements(userId: String): Map<String, Boolean> {
        val doc = firestore.collection("users").document(userId).get().await()
        return doc.get("unlockedAchievements") as? Map<String, Boolean> ?: mapOf()
    }

    override suspend fun updateUserAchievements(
        userId: String,
        unlocked: Map<String, Boolean>
    ) {
        FirebaseAuth.getInstance().currentUser?.uid
        val data = mapOf(
            "unlockedAchievements" to unlocked
        )
        firestore.collection("users").document(userId).update(data).await()
    }

    override suspend fun updateScanCount(userId: String, newScanCount: Int) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("scanCount", newScanCount).await()
    }

    override suspend fun getScanCount(userId: String): Int {
        val userRef = firestore.collection("users").document(userId)
        val snapshot = userRef.get().await()
        return snapshot.getLong("scanCount")?.toInt() ?: 0
    }

    override suspend fun getCurrentUser(): User {
        val userId = auth.currentUser?.uid.orEmpty()
        val userRef = firestore.collection("users").document(userId)
        val snapshot = userRef.get().await()
        val username = snapshot.getString("name") ?: ""
        val email = snapshot.getString("email") ?: ""
        val level = snapshot.getString("level") ?: ""
        val scanCount = snapshot.getLong("scanCount") ?: 0L
        return User(
            id = userId,
            username = username,
            email = email,
            level = level,
            scanCount = scanCount.toInt()
        )
    }

    override suspend fun getRecentPredictions(userId: String): List<Predict> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("predictions")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(4)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val predictedAllergen = doc.getString("predicted_allergen")
                val hasAllergy = doc.getBoolean("hasAllergy")
                val timestamp = doc.getTimestamp("timestamp")

                if (predictedAllergen != null && hasAllergy != null && timestamp != null) {
                    Predict(predictedAllergen, hasAllergy, timestamp)
                } else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }



    override suspend fun clearUser() {
        try {
            // Log out from Firebase
            auth.signOut()

            // Example: clearUserDataFromLocalStorage()
            sharedPreference.clearUserSession()
        } catch (e: Exception) {
            // Handle any error that may occur during logout
            throw Exception("Error while clearing user: ${e.message}")
        }
    }
}