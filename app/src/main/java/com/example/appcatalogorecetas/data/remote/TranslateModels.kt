package com.example.appcatalogorecetas.data.remote

import com.google.gson.annotations.SerializedName

data class TraduccionResponse(
    @SerializedName("responseData")
    val responseData: TraduccionData
)

data class TraduccionData(
    @SerializedName("translatedText")
    val translatedText: String
)
