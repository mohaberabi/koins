package com.plcoding.cryptotracker.features.crypto.domain.source

import com.plcoding.cryptotracker.features.crypto.data.util.AppResult
import com.plcoding.cryptotracker.features.crypto.data.util.NetworkError
import com.plcoding.cryptotracker.features.crypto.domain.model.CoinModel
import com.plcoding.cryptotracker.features.crypto.domain.model.CoinPrice
import java.time.ZonedDateTime

interface CoinRemoteDataSource {


    suspend fun getCoins(
    ): AppResult<List<CoinModel>, NetworkError>


    suspend fun getCoinHistory(
        id: String,
        start: ZonedDateTime,
        end: ZonedDateTime,
    ): AppResult<List<CoinPrice>, NetworkError>
}