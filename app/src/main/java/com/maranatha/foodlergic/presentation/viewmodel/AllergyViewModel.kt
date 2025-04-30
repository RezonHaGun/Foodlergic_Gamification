package com.maranatha.foodlergic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.maranatha.foodlergic.R
import com.maranatha.foodlergic.data.models.Allergy
import com.maranatha.foodlergic.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AllergyViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _userAllergies = MutableLiveData<Resource<List<Allergy>>>()
    val userAllergies: LiveData<Resource<List<Allergy>>> = _userAllergies

    fun getUserAllergies() {
        viewModelScope.launch {
            _userAllergies.value = Resource.Loading()
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(firebaseAuth.currentUser?.uid.orEmpty())
                    .get()
                    .await()

                val allergiesMap = snapshot.get("allergies") as? Map<String, Boolean> ?: emptyMap()
                val mappedAllergies = allergiesMap.map { (name, isSelected) ->
                    val image = when (name.lowercase()) {
                        "ikan" -> R.drawable.fish_image
                        "kerang" -> R.drawable.shellfish_image
                        "kepiting" -> R.drawable.crab_image
                        "udang" -> R.drawable.shrimp_image
                        else -> R.drawable.fish_image
                    }

                    val displayName = when (name.lowercase()) {
                        "ikan" -> "Ikan"
                        "kerang" -> "Kerang"
                        "kepiting" -> "Kepiting"
                        "udang" -> "Udang"
                        else -> name // biarkan seperti aslinya kalau bukan salah satu dari empat ini
                    }
                    Allergy(name = displayName, image = image, isSelected = isSelected)
                }

                _userAllergies.value = Resource.Success(mappedAllergies)
            } catch (e: Exception) {
                _userAllergies.value = Resource.Error("Error fetching allergies: ${e.message}")
            }
        }
    }

    fun isAllergic(label: String): Boolean {
        val allergyNames = _userAllergies.value?.data?.map { it.name }.orEmpty()
        return label in allergyNames
    }

    private val _submitResult = MutableLiveData<Resource<String>>()
    val submitResult: LiveData<Resource<String>> = _submitResult

    fun submitAllergies(allergyNames: List<String>) {
        viewModelScope.launch {
            _submitResult.value = Resource.Loading()
            try {
                val userId = firebaseAuth.currentUser?.uid
                if (userId == null) {
                    _submitResult.value = Resource.Error("User tidak ditemukan")
                    return@launch
                }

                val allAllergyOptions = listOf("ikan", "udang", "kepiting", "kerang")
                val allergyMap = allAllergyOptions.associateWith { it in allergyNames }

                val userAllergyData = mapOf(
                    "allergies" to allergyMap,
                    "isAllergySubmitted" to true
                )

                firestore.collection("users")
                    .document(userId)
                    .set(userAllergyData, SetOptions.merge())
                    .addOnSuccessListener {
                        _submitResult.value = Resource.Success("Allergies berhasil disimpan")
                    }
                    .addOnFailureListener { e ->
                        _submitResult.value = Resource.Error("Gagal menyimpan: ${e.message}")
                    }

            } catch (e: Exception) {
                _submitResult.value = Resource.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }
}