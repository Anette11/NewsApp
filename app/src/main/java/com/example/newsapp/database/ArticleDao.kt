package com.example.newsapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsapp.models.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticle(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllSavedArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}