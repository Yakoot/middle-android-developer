package ru.skillbranch.skillarticles.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData

class NetworkMonitor(context: Context) {
    var isConnected: Boolean = false
    val isConnectedLive = MutableLiveData(false)
    val networkTypeLive = MutableLiveData(NetworkType.NONE)

    private val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setNetworkIsConnected(isConnected:Boolean = true) { this.isConnected = isConnected }

    fun registerNetworkMonitor() {
        obtainNetworkType(cm.activeNetwork?.let { cm.getNetworkCapabilities(it) })
            .also { networkTypeLive.postValue(it) }

        cm.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            object: ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.d("ConnectivityManager", "onAvailable")
                    isConnected = true
                    isConnectedLive.postValue(true)
                }

                override fun onLost(network: Network) {
                    Log.d("ConnectivityManager", "onLost")
                    isConnected = false
                    isConnectedLive.postValue(false)
                    networkTypeLive.postValue(NetworkType.NONE)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    Log.d("ConnectivityManager", "onCapabilitiesChanged")
                    networkTypeLive.postValue(obtainNetworkType(networkCapabilities))
                }
            }
        )
    }

    private fun obtainNetworkType(networkCapabilities: NetworkCapabilities?): NetworkType = when {
        networkCapabilities == null -> NetworkType.NONE
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
        else -> NetworkType.UNKNOWN
    }

}

enum class NetworkType {
    NONE, UNKNOWN, WIFI, CELLULAR
}