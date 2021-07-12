package com.example.newsapp.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.database.ArticleDao
import com.example.newsapp.models.Article

class ArticleRepository(
    private val articleDao: ArticleDao
) {
    suspend fun getHeadlines(page: Int) =
        RetrofitInstance.articleWebservice.getHeadlines(page = page)

    suspend fun searchByKeyword(searchKeyword: String, page: Int) =
        RetrofitInstance.articleWebservice.searchByKeyword(
            searchKeyword = searchKeyword,
            page = page
        )

    suspend fun saveArticle(article: Article) =
        articleDao.saveArticle(article)

    fun getAllSavedArticles(): LiveData<List<Article>> =
        articleDao.getAllSavedArticles()

    suspend fun deleteArticle(article: Article) =
        articleDao.deleteArticle(article)
}