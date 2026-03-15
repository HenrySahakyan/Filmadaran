package com.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Show(
    val id: Int,
    val name: String,
    val type: String?,
    val language: String?,
    val genres: List<String>?,
    val status: String?,
    val premiered: String?,
    val officialSite: String?,
    val rating: Rating?,
    val image: Image?,
    val summary: String?
) : Parcelable

@Parcelize
data class Rating(
    val average: Double?
) : Parcelable

@Parcelize
data class Image(
    val medium: String?,
    val original: String?
) : Parcelable
