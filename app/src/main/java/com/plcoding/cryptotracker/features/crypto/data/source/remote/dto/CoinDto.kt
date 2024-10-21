package com.plcoding.cryptotracker.features.crypto.data.source.remote.dto

import com.plcoding.cryptotracker.features.crypto.domain.model.CoinModel
import kotlinx.serialization.Serializable


@Serializable
data class CoinDto(
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUsd: Double,
    val priceUsd: Double,
    val changePercent24Hr: Double
)

@Serializable
data class CoinResponseDto(
    val data: List<CoinDto>
)

fun CoinDto.toCoinModel() = CoinModel(
    id,
    rank,
    name,
    symbol,
    marketCapUs = marketCapUsd,
    priceUsd,
    changePercent24Hr,
)