package com.plcoding.cryptotracker.features.crypto.presentation.model

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min

data class DrawPoint(


    val x: Float,
    val y: Float,
    val xLabel: String
)


data class ValueLabel(
    val value: Float,
    val unit: String
) {
    fun formatted(): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            val fractionalDigit = when {
                value > 1000 -> 0
                value in 2f..999f -> 2
                else -> 3
            }
            maximumFractionDigits = fractionalDigit
            minimumIntegerDigits = 0
        }
        return "${formatter.format(value)} $unit"
    }
}