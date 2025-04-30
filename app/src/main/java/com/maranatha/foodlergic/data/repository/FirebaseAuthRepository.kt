package com.maranatha.foodlergic.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maranatha.foodlergic.data.Preference
import com.maranatha.foodlergic.domain.models.User
import com.maranatha.foodlergic.domain.repository.IAuthRepository
import com.maranatha.foodlergic.utils.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val sessionManager: Preference
) : IAuthRepository {
    override suspend fun login(email: String, password: String): Resource<User> {
        return runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Login failed")

            val firestore = FirebaseFirestore.getInstance()
            val snapshot = firestore.collection("users").document(firebaseUser.uid).get().await()
            val isAllergySubmitted = snapshot.getBoolean("isAllergySubmitted") ?: false

            val user = User(
                id = firebaseUser.uid,
                username = firebaseUser.displayName ?: "",
                email = email,
                isAllergySubmitted = isAllergySubmitted
            )
            sessionManager.saveUserSession(user)

            user
        }.fold(
            onSuccess = { user -> Resource.Success(user) },
            onFailure = { error -> Resource.Error("An error occurred: ${error.message}") }
        )
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Resource<Boolean> {
        return runCatching {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Registration failed")

            val user = User(firebaseUser.uid, name, email)
            sessionManager.saveUserSession(user)

            val allergies = mapOf(
                "ikan" to false,
                "udang" to false,
                "kepiting" to false,
                "kerang" to false
            )

            val userData = mapOf(
                "uid" to firebaseUser.uid,
                "name" to name,
                "email" to email,
                "scanCount" to 0,
                "scanStreak" to 0,
                "lastScanDate" to com.google.firebase.Timestamp.now(),
                "unlockedAchievements" to emptyMap<String, Boolean>(),
//                "level" to "Rookie",
                "allergies" to  allergies,
                "isAllergySubmitted" to false,
            )

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(firebaseUser.uid)
                .set(userData)
                .await()
            true
        }.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { error -> Resource.Error("An error occurred: ${error.message}") }
        )
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun logout() {
        firebaseAuth.signOut()
        sessionManager.clearUserSession()
    }
}