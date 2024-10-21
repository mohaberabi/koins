package com.plcoding.cryptotracker.features.crypto.domain.model

import java.time.ZonedDateTime

data class CoinPrice(


    val priceUsd: Double,
    val dateTime: ZonedDateTime,

    )
