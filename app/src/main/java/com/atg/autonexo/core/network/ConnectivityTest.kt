package com.atg.autonexo.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityTest @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "ConnectivityTest"
        private const val BACKEND_HOST = "autonexo-backend-akcsb5avacemdwh7.canadacentral-01.azurewebsites.net"
    }
    
    suspend fun testConnectivity(): ConnectivityResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Testing connectivity...")
            
            // 1. Verificar conexión a Internet
            if (!isNetworkAvailable()) {
                Log.e(TAG, "No network available")
                return@withContext ConnectivityResult.NoNetwork
            }
            
            // 2. Verificar resolución DNS
            val canResolveHost = try {
                val address = InetAddress.getByName(BACKEND_HOST)
                Log.d(TAG, "DNS resolved: ${address.hostAddress}")
                true
            } catch (e: Exception) {
                Log.e(TAG, "DNS resolution failed", e)
                false
            }
            
            if (!canResolveHost) {
                return@withContext ConnectivityResult.DnsResolutionFailed
            }
            
            Log.d(TAG, "Connectivity test passed")
            return@withContext ConnectivityResult.Success
            
        } catch (e: Exception) {
            Log.e(TAG, "Connectivity test failed", e)
            return@withContext ConnectivityResult.UnknownError(e.message ?: "Unknown error")
        }
    }
    
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}


