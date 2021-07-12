package com.example.newsapp.api

import com.example.newsapp.util.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val articleWebservice: ArticleWebservice by lazy {
        retrofitInstance.create(ArticleWebservice::class.java)
    }
}