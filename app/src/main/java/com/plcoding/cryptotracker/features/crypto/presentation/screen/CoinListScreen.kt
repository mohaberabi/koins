package com.plcoding.cryptotracker.features.crypto.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.cryptotracker.features.crypto.presentation.compose.CoinListItem
import com.plcoding.cryptotracker.features.crypto.presentation.model.CoinUiModel
import com.plcoding.cryptotracker.features.crypto.presentation.viewmodel.CoinListState
import com.plcoding.cryptotracker.features.crypto.presentation.viewmodel.CoinListViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun CoinListScreenRoot(
    modifier: Modifier = Modifier,
    viewmodel: CoinListViewModel = koinViewModel()
) {

    val state by viewmodel.state.collectAsStateWithLifecycle()
    state.selected?.let {
        CoinDetailScreen(
            coinListState = state,
        )
    } ?: CoinListScreen(
        state = state,
        modifier = modifier,
        onCoinClick = viewmodel::onCoinClick
    )
}

@Composable
fun CoinListScreen(
    modifier: Modifier = Modifier,
    state: CoinListState,
    onCoinClick: (CoinUiModel) -> Unit
) {

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (state.loading) {

            CircularProgressIndicator()

        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    state.coins,
                ) { coin ->
                    CoinListItem(
                        coin = coin,
                        onClick = {
                            onCoinClick(coin)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    HorizontalDivider()
                }

            }
        }
    }
}