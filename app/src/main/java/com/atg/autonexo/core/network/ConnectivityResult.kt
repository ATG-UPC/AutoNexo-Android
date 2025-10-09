package com.atg.autonexo.core.network

sealed class ConnectivityResult {
    object Success : ConnectivityResult()
    object NoNetwork : ConnectivityResult()
    object DnsResolutionFailed : ConnectivityResult()
    data class UnknownError(val message: String) : ConnectivityResult()
}

