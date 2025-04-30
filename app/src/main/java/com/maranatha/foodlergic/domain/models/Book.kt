package com.maranatha.foodlergic.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val image: Int,
    val name: String,
    val code: String,
    val urlBook: String,
    val threshold: Int,
    val summary: String
) : Parcelable