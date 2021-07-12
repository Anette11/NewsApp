package com.example.newsapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class ConnectivityLiveData(application: ArticleApplication) : LiveData<Boolean>() {
    private val connectivityManager: ConnectivityManager = application
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val connectivityManagerNetworkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(false)
            }
        }

    override fun onActive() {
        super.onActive()
        val networkRequestBuilder = NetworkRequest.Builder()

        connectivityManager.registerNetworkCallback(
            networkRequestBuilder.build(),
            connectivityManagerNetworkCallback
        )
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectivityManagerNetworkCallback)
    }
}