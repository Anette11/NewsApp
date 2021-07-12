package com.example.newsapp.models

data class Headlines(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)