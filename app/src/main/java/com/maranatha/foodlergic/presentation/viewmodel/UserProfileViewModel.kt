package com.maranatha.foodlergic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maranatha.foodlergic.domain.models.Achievement
import com.maranatha.foodlergic.domain.models.Predict
import com.maranatha.foodlergic.domain.models.User
import com.maranatha.foodlergic.domain.repository.UserRepository
import com.maranatha.foodlergic.domain.usecase.AchievementManager
import com.maranatha.foodlergic.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepo: UserRepository
) : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    fun getUserData() {
        viewModelScope.launch {
            try {
                val user = userRepo.getCurrentUser()
                _userData.value = user
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Failed to get user data", e)
                _userData.value = User(
                    id = "",
                    email = "",
                    username = "guest",
                )
            }
        }
    }

    private val _userAchievements = MutableLiveData<Resource<List<Achievement>>>()
    val userAchievements: LiveData<Resource<List<Achievement>>> = _userAchievements

    // Fungsi untuk mengambil data achievement user
    fun getAchievement() {
        viewModelScope.launch {
            try {
                val user = userRepo.getCurrentUser()
                // Memanggil repository untuk mendapatkan achievement user
                val achievements =
                    userRepo.getUserAchievements(user.id) // Gantikan dengan implementasi yang sesuai
                val unlockedMap = achievements as? Map<String, Boolean> ?: mapOf()

                val achievementManager = AchievementManager()
                val newlyUnlocked = achievementManager.achievements.filter {
                    unlockedMap[it.name] == true
                }
                _userAchievements.value =
                    Resource.Success(newlyUnlocked) // Update LiveData dengan data user
            } catch (e: Exception) {
                // Tangani error jika gagal mendapatkan data
                _userAchievements.value = Resource.Error("failed to get user achievements")
            }
        }
    }

    private val _setAchievement = MutableLiveData<Resource<Boolean>>()
    val setAchievement: LiveData<Resource<Boolean>> = _setAchievement
    fun setAchievement(achievement: Achievement) {
        viewModelScope.launch {
            try {
                val user = userRepo.getCurrentUser()
                // Memanggil repository untuk mendapatkan achievement user
                val data = mapOf(
                    achievement.name to true
                )
                userRepo.updateUserAchievements(user.id, data)

                _setAchievement.value = Resource.Success(true)
            } catch (e: Exception) {
                // Tangani error jika gagal mendapatkan data
                _setAchievement.value = Resource.Error("failed to set user achievements")
            }
        }
    }

    private val _recentPredictions = MutableLiveData<List<Predict>>()
    val recentPredictions: LiveData<List<Predict>> = _recentPredictions

    fun fetchRecentPredictions() {
        viewModelScope.launch {
            try {
                val user = userRepo.getCurrentUser()
                val predictions = userRepo.getRecentPredictions(user.id)
                _recentPredictions.value = predictions
            } catch (e: Exception) {
                _recentPredictions.value = emptyList()
            }
        }
    }


    private val _clearUserStatus = MutableLiveData<Resource<Boolean>>()
    val clearUserStatus: LiveData<Resource<Boolean>> = _clearUserStatus

    fun clearSession() {
        viewModelScope.launch {
            _clearUserStatus.value = Resource.Loading()
            try {
                userRepo.clearUser()

                // Jika sukses, update status LiveData
                _clearUserStatus.value = Resource.Success(true)
            } catch (e: Exception) {
                // Jika gagal, update status dengan error
                _clearUserStatus.value = Resource.Error("Error while clearing user: ${e.message}")
            }
        }
    }

}