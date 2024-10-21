package com.plcoding.cryptotracker.features.crypto.data.source.remote

import com.plcoding.cryptotracker.features.crypto.data.networking.constructUrl
import com.plcoding.cryptotracker.features.crypto.data.networking.safeCall
import com.plcoding.cryptotracker.features.crypto.data.source.remote.dto.CoinDto
import com.plcoding.cryptotracker.features.crypto.data.source.remote.dto.CoinHistoryDto
import com.plcoding.cryptotracker.features.crypto.data.source.remote.dto.CoinResponseDto
import com.plcoding.cryptotracker.features.crypto.data.source.remote.dto.toCoinModel
import com.plcoding.cryptotracker.features.crypto.data.source.remote.dto.toCoinPrice
import com.plcoding.cryptotracker.features.crypto.data.util.AppResult
import com.plcoding.cryptotracker.features.crypto.data.util.NetworkError
import com.plcoding.cryptotracker.features.crypto.data.util.map
import com.plcoding.cryptotracker.features.crypto.domain.model.CoinModel
import com.plcoding.cryptotracker.features.crypto.domain.model.CoinPrice
import com.plcoding.cryptotracker.features.crypto.domain.source.CoinRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.ZoneId
import java.time.ZonedDateTime

class KtorCoinsRemoteDatasource(
    private val client: HttpClient
) : CoinRemoteDataSource {
    override suspend fun getCoins(): AppResult<List<CoinModel>, NetworkError> {
        return safeCall<CoinResponseDto> {
            client.get(urlString = constructUrl("/assets"))
        }.map { response ->
            response.data.map { it.toCoinModel() }
        }
    }

    override suspend fun getCoinHistory(
        id: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): AppResult<List<CoinPrice>, NetworkError> {
        return safeCall<CoinHistoryDto> {
            client.get(
                urlString = constructUrl("assets/$id/history"),
            ) {
                parameter("interval", "h6")
                parameter(
                    "start",
                    start.toUtcMillis,
                )
                parameter(
                    "end",
                    end.toUtcMillis,
                )
            }
        }.map { response ->
            response.data.map { it.toCoinPrice() }
        }
    }
}


val ZonedDateTime.toUtcMillis
    get() = withZoneSameInstant(ZoneId.of("UTC")).toInstant().toEpochMilli()