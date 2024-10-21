package com.plcoding.cryptotracker.features.crypto.data.networking

import com.plcoding.cryptotracker.features.crypto.data.util.AppResult
import com.plcoding.cryptotracker.features.crypto.data.util.NetworkError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import kotlin.coroutines.coroutineContext


suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): AppResult<T, NetworkError> {

    return when (response.status.value) {
        in 200..299 -> {
            try {
                AppResult.Done(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                AppResult.Failure(NetworkError.SerializationError)
            }
        }

        408 -> AppResult.Failure(NetworkError.RequestError)
        429 -> AppResult.Failure(NetworkError.TooManyRequest)
        in 500..599 -> AppResult.Failure(NetworkError.ServerError)
        else -> AppResult.Failure(NetworkError.Unknown)
    }
}


suspend inline fun <reified T> safeCall(
    block: () -> HttpResponse
): AppResult<T, NetworkError> {
    val response = try {
        block()
    } catch (e: UnresolvedAddressException) {
        return AppResult.Failure(NetworkError.NoNetworkConnection)
    } catch (e: SerializationException) {
        return AppResult.Failure(NetworkError.SerializationError)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        return AppResult.Failure(NetworkError.Unknown)
    }
    return responseToResult(response)
}