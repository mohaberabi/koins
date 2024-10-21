package com.plcoding.cryptotracker.features.crypto.presentation.model

import android.icu.number.NumberFormatter
import androidx.annotation.DrawableRes
import com.plcoding.cryptotracker.features.crypto.domain.model.CoinModel
import com.plcoding.cryptotracker.features.crypto.domain.model.CoinPrice
import com.plcoding.cryptotracker.util.getDrawableIdForCoin
import java.text.NumberFormat
import java.util.Locale

data class CoinUiModel(


    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUsd: DisplayNumber,
    val priceUsd: DisplayNumber,
    val changePercent24Hr: DisplayNumber,
    @DrawableRes val iconRes: Int,
    val history: List<DrawPoint> = listOf(),
)


data class DisplayNumber(
    val value: Double,
    val formatted: String
)


fun CoinModel.toCoinUi() = CoinUiModel(
    id = id,
    name = name,
    symbol = symbol,
    rank = rank,
    priceUsd = priceUsd.toReadable(),
    changePercent24Hr = changePrice24Hr.toReadable(),
    iconRes = getDrawableIdForCoin(symbol),
    marketCapUsd = marketCapUs.toReadable()
)


fun Double.toReadable(): DisplayNumber {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return DisplayNumber(
        value = this,
        formatted = formatter.format(this)
    )
}