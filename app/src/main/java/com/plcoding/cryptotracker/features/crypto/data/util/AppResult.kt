package com.plcoding.cryptotracker.features.crypto.data.util

import com.plcoding.cryptotracker.util.Error


typealias DomainError = Error
typealias EmptyResult<E> = AppResult<Unit, E>
typealias EitherResult<T, E> = AppResult<T, E>

sealed interface AppResult<out T, out E : DomainError> {


    data class Done<D>(val data: D) : AppResult<D, Nothing>
    data class Failure<E : DomainError>(val error: E) : AppResult<Nothing, E>


}

inline fun <T, E : DomainError> EitherResult<T, E>.onDone(
    block: (T) -> Unit,
): EitherResult<T, E> {
    return when (this) {
        is AppResult.Done -> {
            block(this.data)
            AppResult.Done(this.data)
        }

        is AppResult.Failure -> this
    }
}

inline fun <T, E : DomainError> EitherResult<T, E>.onFailure(
    block: (E) -> Unit,
): EitherResult<T, E> {
    return when (this) {
        is AppResult.Done -> this
        is AppResult.Failure -> {
            block(this.error)
            AppResult.Failure(this.error)
        }
    }
}

inline fun <T, E : DomainError, R> AppResult<T, E>.map(
    block: (T) -> R
): AppResult<R, E> {
    return when (this) {
        is AppResult.Done -> AppResult.Done(block(this.data))
        is AppResult.Failure -> AppResult.Failure(this.error)
    }
}

fun <T, E : DomainError> AppResult<T, E>.asEmpty(): EmptyResult<E> = map {}