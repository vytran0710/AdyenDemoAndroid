package com.example.adyendemo.module.network

import com.example.adyendemo.module.network.NetworkResult.Failure
import com.example.adyendemo.module.network.NetworkResult.Success

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Failure(val throwable: Throwable) : NetworkResult<Nothing>()
}

inline fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
    if (this is Success) action(data)
    return this
}

inline fun <T> NetworkResult<T>.onFailure(action: (Throwable) -> Unit): NetworkResult<T> {
    if (this is Failure) action(throwable)
    return this
}