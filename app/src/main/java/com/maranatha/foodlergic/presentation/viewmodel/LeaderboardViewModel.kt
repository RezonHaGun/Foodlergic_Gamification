package com.maranatha.foodlergic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.maranatha.foodlergic.presentation.leaderboard.LeaderboardItem
import com.maranatha.foodlergic.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _leaderboardItems = MutableLiveData<Resource<List<LeaderboardItem>>>()
    val leaderboardItems: LiveData<Resource<List<LeaderboardItem>>> = _leaderboardItems

    fun fetchLeaderboard() {
        viewModelScope.launch {
            _leaderboardItems.value = Resource.Loading()
            try {
                val documents = firestore.collection("leaderboard")
                    .orderBy(
                        "scanCount",
                        Query.Direction.DESCENDING
                    )
                    .get()
                    .await()

                val leaderboard = mutableListOf<LeaderboardItem>()
                var rank = 1
                for (document in documents) {
                    val userName = document.getString("name") ?: "Anonymous"
                    val scanCount = document.getLong("scanCount") ?: 0L

                    leaderboard.add(LeaderboardItem(rank, userName, scanCount.toInt()))
                    rank++
                }

                _leaderboardItems.value = Resource.Success(leaderboard)
            } catch (e: Exception) {
                _leaderboardItems.value =
                    Resource.Error("Error fetching leaderboard: ${e.message}")
            }
        }
    }


    fun submitLeaderboardItem(leaderboardItem: LeaderboardItem) {
        viewModelScope.launch {
            try {
                val leaderboardRef = firestore.collection("leaderboard")
                val newItem = leaderboardRef.add(leaderboardItem).await()
                _leaderboardItems.value =
                    Resource.Success(listOf(leaderboardItem))  // Update UI with new data
            } catch (e: Exception) {
                _leaderboardItems.value =
                    Resource.Error("Failed to submit leaderboard item: ${e.message}")
            }
        }
    }
}
