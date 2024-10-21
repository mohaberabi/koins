package com.plcoding.cryptotracker.features.crypto.data.util

import com.plcoding.cryptotracker.util.Error


enum class NetworkError : Error {
    RequestError,
    ServerError,
    RequestTimeout,
    TooManyRequest,
    SerializationError,
    NoNetworkConnection,

    Unknown,

}