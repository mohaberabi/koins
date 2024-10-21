package com.plcoding.cryptotracker.features.crypto.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.cryptotracker.features.crypto.presentation.model.DisplayNumber
import com.plcoding.cryptotracker.ui.theme.greenBackground


@Composable
fun PriceChangeComposable(
    change: DisplayNumber,
    modifier: Modifier = Modifier,
) {


    val contentColor = if (change.value < 0.0)
        MaterialTheme.colorScheme.onErrorContainer
    else Color.Green


    val bgColor = if (change.value < 0.0)
        MaterialTheme.colorScheme.errorContainer
    else greenBackground



    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100f))
            .background(bgColor)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Icon(
            imageVector = if (change.value < 0.0)
                Icons.Default.KeyboardArrowDown
            else Icons.Default.KeyboardArrowUp,
            contentDescription = null,
            tint = contentColor
        )
        Text(
            text = "${change.formatted} %",
            color = contentColor,
            fontSize = 14.sp
        )


    }
}