package com.example.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.util.ArticleApplication
import com.example.newsapp.util.ConnectivityLiveData

class ArticleActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var connectivityLiveData: ConnectivityLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = activityMainBinding.root
        setContentView(view)
        setViewModel()
        checkIfNetworkConnectionIsAvailable()
        makeArticlesRequestIfNetworkConnectionIsAvailable()
        setNavigation()
    }

    private fun setViewModel() {
        articleViewModel = ViewModelProvider(
            this, ArticleFactory(
                application,
                (application as ArticleApplication).articleRepository
            )
        ).get(ArticleViewModel::class.java)
    }

    private fun makeArticlesRequestIfNetworkConnectionIsAvailable() {
        articleViewModel.isConnected.observe(this, {
            if (it == true && articleViewModel.headlines.value == null) {
                articleViewModel.getHeadlines()
            }
        })
    }

    private fun checkIfNetworkConnectionIsAvailable() {
        connectivityLiveData =
            ConnectivityLiveData(application as ArticleApplication)

        connectivityLiveData.observe(this, {
            if (it != null) {
                articleViewModel.isConnected.postValue(it)
            }
        })
    }

    private fun setNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment?

        NavigationUI.setupWithNavController(
            activityMainBinding.bottomNavigation,
            navHostFragment!!.navController
        )
    }
}