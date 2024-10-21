package com.plcoding.cryptotracker.features.crypto.domain.model

data class CoinModel(
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUs: Double,
    val priceUsd: Double,
    val changePrice24Hr: Double
)
