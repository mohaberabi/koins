package com.plcoding.cryptotracker.features.crypto.data.source.remote.dto

import com.plcoding.cryptotracker.features.crypto.domain.model.CoinPrice
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId


@Serializable
data class CoinPriceDto(
    val time: Long,
    val priceUsd: String
)


fun CoinPriceDto.toCoinPrice() = CoinPrice(
    priceUsd = priceUsd.toDoubleOrNull() ?: 0.0,
    dateTime = Instant.ofEpochMilli(time).atZone(ZoneId.of("UTC"))
)


@Serializable
data class CoinHistoryDto(


    val data: List<CoinPriceDto>
)
