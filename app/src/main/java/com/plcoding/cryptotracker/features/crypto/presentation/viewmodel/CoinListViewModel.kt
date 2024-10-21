package com.plcoding.cryptotracker.features.crypto.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.features.crypto.data.util.onDone
import com.plcoding.cryptotracker.features.crypto.data.util.onFailure
import com.plcoding.cryptotracker.features.crypto.domain.source.CoinRemoteDataSource
import com.plcoding.cryptotracker.features.crypto.presentation.model.CoinUiModel
import com.plcoding.cryptotracker.features.crypto.presentation.model.DrawPoint
import com.plcoding.cryptotracker.features.crypto.presentation.model.toCoinUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CoinListViewModel(
    private val source: CoinRemoteDataSource
) : ViewModel() {


    private val _state = MutableStateFlow(CoinListState())
    val state = _state.onStart {
        loadCoins()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        CoinListState()
    )


    init {
        loadCoins()
    }

    fun onCoinClick(coin: CoinUiModel) {
        _state.update { it.copy(selected = coin) }
        viewModelScope.launch {
            val res = source.getCoinHistory(
                id = coin.id,
                start = ZonedDateTime.now().minusDays(5),
                end = ZonedDateTime.now()
            )
            res.onDone { history ->

                val points = history
                    .sortedBy { it.dateTime }
                    .map {
                        DrawPoint(
                            x = it.dateTime.hour.toFloat(),
                            y = it.priceUsd.toFloat(),
                            xLabel = DateTimeFormatter.ofPattern("ha\nM/d").format(it.dateTime)
                        )
                    }
                _state.update {
                    it.copy(
                        selected = it.selected?.copy(
                            history = points
                        )
                    )
                }
            }
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            val res = source.getCoins()
            res.onDone { coins ->
                _state.update { it.copy(coins = coins.map { coin -> coin.toCoinUi() }) }
            }.onFailure { error ->

            }
            _state.update { it.copy(loading = false) }
        }
    }
}