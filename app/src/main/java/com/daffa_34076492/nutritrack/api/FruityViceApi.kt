package com.yourpackage.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FruityViceService {
    private const val BASE_URL = "https://www.fruityvice.com/"

    val api: FruityViceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FruityViceApi::class.java)
    }
}
