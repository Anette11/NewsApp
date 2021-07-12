package com.example.newsapp.api

import com.example.newsapp.models.Headlines
import com.example.newsapp.util.Constants.API_KEY
import com.example.newsapp.util.Constants.COUNTRY
import com.example.newsapp.util.Constants.LANGUAGE
import com.example.newsapp.util.Constants.PAGE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleWebservice {
    @GET("/v2/top-headlines")
    suspend fun getHeadlines(
        @Query("country") country: String = COUNTRY,
        @Query("page") page: Int = PAGE,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<Headlines>

    @GET("/v2/everything")
    suspend fun searchByKeyword(
        @Query("q") searchKeyword: String,
        @Query("language") language: String = LANGUAGE,
        @Query("page") page: Int = PAGE,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<Headlines>
}