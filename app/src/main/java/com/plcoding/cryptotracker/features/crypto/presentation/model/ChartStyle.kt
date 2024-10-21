package com.plcoding.cryptotracker.features.crypto.presentation.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit


data class ChartStyle(
    val chartLineColor: Color,
    val unselectedColor: Color,
    val selectedColor: Color,
    val helperLinesThickness: Float,
    val axisLinesThickensPxs: Float,
    val labelFontSize: TextUnit,
    val minYLabelDp: Dp,
    val verticalPadding: Dp,
    val verticalSpacing: Dp,
    val horizontalPadding: Dp,
    val xAxisLabelSpacing: Dp
)