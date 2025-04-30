package com.maranatha.foodlergic.domain.models

data class Achievement(
    val iconResId: Int,
    val lockedIconResId: Int,
    val name: String,
    val description: String,
    val type: String,
    val threshold: Int,
    val isUnlocked: Boolean = false
)
