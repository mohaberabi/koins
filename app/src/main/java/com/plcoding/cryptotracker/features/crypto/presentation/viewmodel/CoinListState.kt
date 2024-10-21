package com.plcoding.cryptotracker.features.crypto.presentation.viewmodel

import androidx.compose.runtime.Immutable
import com.plcoding.cryptotracker.features.crypto.presentation.model.CoinUiModel


@Immutable
data class CoinListState(
    val loading: Boolean = false,
    val coins: List<CoinUiModel> = emptyList(),
    val selected: CoinUiModel? = null,
)
