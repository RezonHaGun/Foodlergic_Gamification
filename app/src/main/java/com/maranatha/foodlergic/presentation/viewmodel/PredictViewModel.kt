package com.maranatha.foodlergic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.maranatha.foodlergic.domain.models.Predict
import com.maranatha.foodlergic.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PredictViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _status = MutableLiveData<Resource<Predict>>()
    val status: LiveData<Resource<Predict>> = _status

    fun predictAndSave(
        predictedAllergen: String,
        hasAllergy: Boolean
    ) {
        viewModelScope.launch {
            _status.value = Resource.Loading()

            val predict = savePredictionToFirestore(predictedAllergen, hasAllergy)
            if (predict != null) {
                _status.value = Resource.Success(predict)
            } else {
                _status.value = Resource.Error("Gagal menyimpan prediksi ke Firestore")
            }
        }
    }

    private suspend fun savePredictionToFirestore(
        predictionResult: String,
        hasAllergy: Boolean
    ): Predict? {
        val userId = auth.currentUser?.uid ?: return null

        val predictionData = mapOf(
            "timestamp" to com.google.firebase.Timestamp.now(),
            "predicted_allergen" to predictionResult,
            "hasAllergy" to hasAllergy
        )

        return try {
            firestore.collection("users")
                .document(userId)
                .collection("predictions")
                .add(predictionData)
                .await()

            firestore.collection("users")
                .document(userId)
                .update("scanCount", FieldValue.increment(1))
                .await()

            val userDoc = firestore.collection("users").document(userId).get().await()
            val userName = userDoc.getString("name") ?: "Anonymous"
            val scanCount = userDoc.getLong("scanCount") ?: 1L

            val level = when {
                scanCount < 100 -> "Rookie"
                scanCount < 1000 -> "Beginner"
                scanCount < 30000 -> "Explorer"
                scanCount < 60000 -> "Expert"
                else -> "Master Scanner"
            }

            // 🔁 Update user level in Firestore
            firestore.collection("users")
                .document(userId)
                .update("level", level)
                .await()

            val leaderboardDocRef = firestore.collection("leaderboard").document(userId)
            leaderboardDocRef.set(
                mapOf(
                    "userId" to userId,
                    "name" to userName,
                    "scanCount" to scanCount,
                    "lastUpdated" to com.google.firebase.Timestamp.now()
                ),
                SetOptions.merge()
            ).await()

            Log.d("PREDICTION", "Prediction saved successfully")
            Predict(predictionResult, hasAllergy, com.google.firebase.Timestamp.now())
        } catch (e: Exception) {
            Log.e("PREDICTION", "Failed to save prediction: ${e.message}")
            null
        }
    }

    fun clearStatus() {
        _status.value = null
    }
}
