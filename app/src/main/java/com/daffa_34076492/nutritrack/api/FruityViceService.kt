package com.yourpackage.data.api

import com.yourpackage.data.model.FruitInfo
import retrofit2.http.GET
import retrofit2.http.Path


interface FruityViceApi {
    @GET("api/fruit/{name}")
    suspend fun getFruitDetails(@Path("name") name: String): FruitInfo


}

