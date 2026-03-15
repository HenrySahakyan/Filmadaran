package com.data.local.entity

import androidx.room.*
import com.domain.model.Image
import com.domain.model.Rating

@Entity(
    tableName = "favorite_shows",
    indices = [Index(value = ["name"])]
)
data class FavoriteShowEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: String?,
    val language: String?,
    val genres: List<String>?,
    val status: String?,
    val premiered: String?,
    val officialSite: String?,
    val ratingAverage: Double?,
    val imageMedium: String?,
    val imageOriginal: String?,
    val summary: String?,
    val createdAt: Long = System.currentTimeMillis()
)
