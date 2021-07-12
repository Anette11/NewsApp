package com.example.newsapp.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.newsapp.models.Article
import com.example.newsapp.repository.ArticleRepository
import kotlinx.coroutines.launch

class ArticleViewModel(
    application: Application,
    private val articleRepository: ArticleRepository
) : AndroidViewModel(application) {
    private var headlinesPageNumber = 1
    private var searchByKeywordPageNumber = 1

    val isConnected = MutableLiveData(false)
    val articleToShowInWebView = MutableLiveData<Article>()
    val displayedProgressBarWebView = MutableLiveData<Int>()
    val editTextSearchKeyword = MutableLiveData<String>()

    private val _displayedProgressBarBreakingNews = MutableLiveData<Int>()
    val displayedProgressBarBreakingNews: LiveData<Int>
        get() = _displayedProgressBarBreakingNews

    private val _displayedProgressBarSearch = MutableLiveData<Int>()
    val displayedProgressBarSearch: LiveData<Int>
        get() = _displayedProgressBarSearch

    private val _headlines = MutableLiveData<List<Article>>()
    val headlines: LiveData<List<Article>>
        get() = _headlines

    private val _newsSearched = MutableLiveData<List<Article>>()
    val newsSearched: LiveData<List<Article>>
        get() = _newsSearched

    val allSavedArticles: LiveData<List<Article>> =
        articleRepository.getAllSavedArticles()

    fun getHeadlines() = viewModelScope.launch {
        _displayedProgressBarBreakingNews.postValue(1)
        if (isConnected.value == true) {
            val response = articleRepository.getHeadlines(headlinesPageNumber)
            headlinesPageNumber++

            if (response.body()?.articles != null) {
                var oldArticles = headlines.value?.toMutableList()
                val newArticles = response.body()?.articles?.toMutableList()
                if (newArticles != null && oldArticles == null) {
                    oldArticles = newArticles
                } else if (newArticles != null && oldArticles != null) {
                    oldArticles.addAll(newArticles)
                }
                _headlines.postValue(oldArticles!!)
            } else {
                _headlines.postValue(response.body()?.articles)
            }
        }
        _displayedProgressBarBreakingNews.postValue(0)
    }

    fun searchByKeywordLoadMore(searchKeyword: String) = viewModelScope.launch {
        _displayedProgressBarSearch.postValue(1)
        searchByKeywordPageNumber++
        if (isConnected.value == true) {
            val response = articleRepository
                .searchByKeyword(searchKeyword, searchByKeywordPageNumber)

            if (response.body()?.articles != null) {
                var oldArticles = newsSearched.value?.toMutableList()
                val newArticles = response.body()?.articles?.toMutableList()
                if (newArticles != null && oldArticles == null) {
                    oldArticles = newArticles
                } else if (newArticles != null && oldArticles != null) {
                    oldArticles.addAll(newArticles)
                }
                _newsSearched.postValue(oldArticles!!)
            } else {
                _newsSearched.postValue(response.body()?.articles)
            }
        }
        _displayedProgressBarSearch.postValue(0)
    }

    fun searchByKeyword(searchKeyword: String) = viewModelScope.launch {
        _displayedProgressBarSearch.postValue(1)
        searchByKeywordPageNumber = 1
        if (isConnected.value == true) {
            val response = articleRepository
                .searchByKeyword(searchKeyword, searchByKeywordPageNumber)
            _newsSearched.postValue(response.body()?.articles)
        }
        _displayedProgressBarSearch.postValue(0)
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        articleRepository.saveArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        articleRepository.deleteArticle(article)
    }
}