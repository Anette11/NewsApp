package com.example.newsapp.util

import android.app.Application
import com.example.newsapp.database.ArticleDatabase
import com.example.newsapp.repository.ArticleRepository

class ArticleApplication : Application() {
    private val articleDatabase by lazy { ArticleDatabase.getArticleDatabase(this) }
    val articleRepository by lazy { ArticleRepository(articleDatabase.articleDao()) }
}