package com.example.newsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.repository.ArticleRepository

class ArticleFactory(
    private val application: Application,
    private val articleRepository: ArticleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(application, articleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}