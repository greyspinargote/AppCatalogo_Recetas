package com.example.appcatalogorecetas.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TranslateApiService {
    @GET("get")
    suspend fun traducir(
        @Query("q") texto: String,
        @Query("langpair") parIdiomas: String = "en|es"
    ): TraduccionResponse
}