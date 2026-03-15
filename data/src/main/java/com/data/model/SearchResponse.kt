package com.data.model

import com.domain.model.Show

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("score")
    val score: Double?,
    @SerializedName("show")
    val show: Show
)
