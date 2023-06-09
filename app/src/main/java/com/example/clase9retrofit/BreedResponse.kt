package com.example.clase9retrofit

import com.google.gson.annotations.SerializedName

data class BreedResponse (
    @SerializedName("message")val images: List<String>,
    val status: String
)