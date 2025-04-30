package com.maranatha.foodlergic.domain.models

data class User(
    val id: String,
    val username: String,
    val email: String,
    val level: String = "",
    val isAllergySubmitted: Boolean = false,
    val profileUrl: String? = "",
    val scanCount: Int = 0
)