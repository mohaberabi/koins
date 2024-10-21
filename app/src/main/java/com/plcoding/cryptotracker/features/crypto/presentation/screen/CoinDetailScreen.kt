package com.plcoding.cryptotracker.features.crypto.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.features.crypto.presentation.compose.CoinInfoCard
import com.plcoding.cryptotracker.features.crypto.presentation.compose.LineChart
import com.plcoding.cryptotracker.features.crypto.presentation.model.ChartStyle
import com.plcoding.cryptotracker.features.crypto.presentation.model.DrawPoint
import com.plcoding.cryptotracker.features.crypto.presentation.model.toReadable
import com.plcoding.cryptotracker.features.crypto.presentation.viewmodel.CoinListState
import com.plcoding.cryptotracker.ui.theme.greenBackground


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CoinDetailScreen(
    modifier: Modifier = Modifier,
    coinListState: CoinListState,
) {


    val contentColor = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Black
    }
    if (coinListState.loading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        coinListState.selected?.let { coin ->

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Icon(
                    imageVector = ImageVector.vectorResource(id = coin.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = coin.name,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 40.sp,
                    color = contentColor,
                )
                Text(
                    text = coin.symbol,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = contentColor,
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    CoinInfoCard(
                        formattedText = coin.marketCapUsd.formatted,
                        icon = ImageVector.vectorResource(id = R.drawable.stock),
                        title = stringResource(R.string.market_cap)
                    )
                    CoinInfoCard(
                        formattedText = coin.priceUsd.formatted,
                        icon = ImageVector.vectorResource(id = R.drawable.dollar),
                        title = stringResource(R.string.price)
                    )
                    val change =
                        (coin.priceUsd.value * (coin.changePercent24Hr.value / 100)).toReadable()
                    val isPositive = coin.changePercent24Hr.value > 0.0
                    val changeColor = if (isPositive) {
                        if (isSystemInDarkTheme()) Color.Green else greenBackground
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                    CoinInfoCard(
                        contentColor = changeColor,
                        formattedText = coin.priceUsd.formatted,
                        icon = ImageVector.vectorResource(
                            id = if (isPositive)
                                R.drawable.trending else R.drawable.trending_down
                        ),
                        title = stringResource(R.string.change_in_last_24_hr, change.formatted)
                    )

                    AnimatedVisibility(visible = coin.history.isNotEmpty()) {
                        var selected by remember {
                            mutableStateOf<DrawPoint?>(null)
                        }

                        val style = ChartStyle(
                            chartLineColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.secondary,
                            selectedColor = MaterialTheme.colorScheme.primary,
                            helperLinesThickness = 5f,
                            minYLabelDp = 25.dp,
                            verticalPadding = 8.dp,
                            horizontalPadding = 8.dp,
                            xAxisLabelSpacing = 8.dp,
                            labelFontSize = 14.sp,
                            axisLinesThickensPxs = 5f,
                            verticalSpacing = 16.dp
                        )
                        LineChart(
                            style = style,
                            visibleDataPointIdx = coin.history.indices,
                            points = coin.history,
                            unit = "$",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16 / 9f),
                            selectedPoint = selected,
                            onSelectPoint = {
                                selected = it
                            }
                        )
                    }
                }
            }

        }
    }
}