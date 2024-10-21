package com.plcoding.cryptotracker.features.crypto.presentation.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.cryptotracker.features.crypto.domain.model.CoinPrice
import com.plcoding.cryptotracker.features.crypto.presentation.model.ChartStyle
import com.plcoding.cryptotracker.features.crypto.presentation.model.DrawPoint
import com.plcoding.cryptotracker.features.crypto.presentation.model.ValueLabel
import com.plcoding.cryptotracker.ui.theme.CryptoTrackerTheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.random.Random


@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    style: ChartStyle,
    visibleDataPointIdx: IntRange,
    points: List<DrawPoint>,
    unit: String,
    selectedPoint: DrawPoint? = null,
    onSelectPoint: (DrawPoint) -> Unit = {},
    onXLabelWidthChange: (Float) -> Unit = {},
    showHelperLines: Boolean = true,
) {
    val textStyle = LocalTextStyle.current.copy(
        fontSize = style.labelFontSize,
    )

    val visibleOnly = remember(
        points,
        visibleDataPointIdx,
    ) {
        points.slice(visibleDataPointIdx)
    }
    val maxYValue = remember(visibleOnly) {
        visibleOnly.maxOfOrNull { it.y } ?: 0f
    }

    val minYValue = remember(visibleOnly) {
        visibleOnly.minOfOrNull { it.y } ?: 0f
    }
    val measurer = rememberTextMeasurer()
    var xLabelWidth by remember {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(key1 = xLabelWidth) {
        onXLabelWidthChange(xLabelWidth)
    }
    val selectedPointIndex = remember(selectedPoint) {
        points.indexOf(selectedPoint)
    }

    var drawPoints by remember {
        mutableStateOf(listOf<DrawPoint>())
    }
    var isShowingSelectedData by remember {
        mutableStateOf(selectedPoint != null)
    }
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(drawPoints, xLabelWidth) {
                detectHorizontalDragGestures { change, _ ->

                    val newSelected = getSelectedPointIndex(
                        touchOffset = change.position.x,
                        triggerWidth = xLabelWidth,
                        points = drawPoints
                    )
                    isShowingSelectedData =
                        (newSelected + visibleDataPointIdx.first) in visibleDataPointIdx

                    if (isShowingSelectedData) {
                        onSelectPoint(points[newSelected])
                    }

                }
            },
    ) {
        val minLabelSpacingY = style.minYLabelDp.toPx()
        val verticalPadding = style.verticalPadding.roundToPx()
        val xAxisLabelSpacing = style.xAxisLabelSpacing.toPx()
        val horizontalPadding = style.horizontalPadding.roundToPx()
        val xLabelTextLayoutResults = visibleOnly.map {
            measurer.measure(
                text = it.xLabel,
                style = textStyle.copy(textAlign = TextAlign.Center)
            )
        }
        val maxXLabelWidth = xLabelTextLayoutResults.maxOfOrNull { it.size.width } ?: 0
        val maxXLabelHeight = xLabelTextLayoutResults.maxOfOrNull { it.size.height } ?: 0
        val maxXLabelLineCount = xLabelTextLayoutResults.maxOfOrNull { it.lineCount } ?: 0
        val xLabelLineHeight = maxXLabelHeight / maxXLabelLineCount
        val extras = (maxXLabelHeight + 2 * verticalPadding + xLabelLineHeight + xAxisLabelSpacing)
        val viewportHeightPx = size.height - extras
        val labelViewPortHeightPx = viewportHeightPx + xLabelLineHeight
        val labelCountNotLast =
            (labelViewPortHeightPx / (xLabelLineHeight + minLabelSpacingY)).toInt()
        val valueIncremental = (maxYValue - minYValue) / labelCountNotLast
        val yLabels = (0..labelCountNotLast).map {
            ValueLabel(
                value = maxYValue - (valueIncremental * it),
                unit = unit
            )
        }
        val yLabelResults = yLabels.map {
            measurer.measure(
                it.formatted(),
                textStyle
            )
        }
        val viewPortTopY = verticalPadding + xLabelLineHeight + 10f
        val viewPortRightX = size.width
        val viewPortBottomY = viewPortTopY + viewportHeightPx
        val viewPortLeftX = (2 * horizontalPadding).toFloat()
        val viewPort = Rect(
            left = viewPortLeftX,
            right = viewPortRightX,
            top = viewPortTopY,
            bottom = viewPortBottomY
        )

        //   drawRect(
        //            color = Color.Green.copy(alpha = 0.25f),
        //            topLeft = viewPort.topLeft,
        //            size = viewPort.size
        //        )
        xLabelWidth = maxXLabelWidth + xAxisLabelSpacing
        xLabelTextLayoutResults.forEachIndexed { index, result ->
            val selected = index == selectedPointIndex
            val color = if (selected) style.selectedColor else style.unselectedColor
            val x = viewPortLeftX + xAxisLabelSpacing / 2f + xLabelWidth * index
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x,
                    y = viewPortBottomY + xAxisLabelSpacing,
                ),
                color = color,
            )
            if (showHelperLines) {
                drawLine(
                    color = style.unselectedColor,
                    start = Offset(
                        x = x + result.size.width.toFloat() / 2f,
                        y = viewPortBottomY,
                    ),
                    end = Offset(
                        x = x + result.size.width.toFloat() / 2f,
                        y = viewPortTopY,
                    ),
                )

            }
            if (selected) {
                val valueLabel = ValueLabel(
                    value = visibleOnly[index].y,
                    unit = unit
                )
                val selectedResult = measurer.measure(
                    text = valueLabel.formatted(),
                    style = textStyle.copy(
                        color = style.selectedColor,
                    ),
                    maxLines = 1
                )
                val textPositionX = if (selectedPointIndex == visibleDataPointIdx.last) {
                    x - selectedResult.size.width
                } else {
                    x - selectedResult.size.width / 2f
                } + selectedResult.size.width / 2f
                val isTextInVisibleRange =
                    (size.width - textPositionX).roundToInt() in 0..size.width.roundToInt()

                if (isTextInVisibleRange) {
                    drawText(
                        textLayoutResult = selectedResult,
                        topLeft = Offset(
                            x = textPositionX,
                            y = viewPortTopY - selectedResult.size.height - 10f
                        )
                    )
                }
            }
        }


        val heightRequiredLabels = xLabelLineHeight * (labelCountNotLast + 1)
        val remainHeightLabels = labelViewPortHeightPx - heightRequiredLabels
        val labelsYSpacing = remainHeightLabels / labelCountNotLast
        val maxYLabelWidth = yLabelResults.maxOfOrNull { it.size.width } ?: 0
        yLabelResults.forEachIndexed { index, textLayoutResult ->
            val selected = index == selectedPointIndex
            val color = if (selected) style.selectedColor else style.unselectedColor
            val x = horizontalPadding + maxYLabelWidth - textLayoutResult.size.width.toFloat()
            val y =
                viewPortTopY + index * (xLabelLineHeight + labelsYSpacing) - xLabelLineHeight / 2f
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = x,
                    y = y
                ),
                color = color,
            )
            if (showHelperLines) {
                drawLine(
                    color = style.unselectedColor,
                    start = Offset(
                        x = viewPortLeftX,
                        y = y + textLayoutResult.size.height.toFloat() / 2f
                    ),
                    end = Offset(
                        x = viewPortRightX,
                        y = y + textLayoutResult.size.height.toFloat() / 2f
                    ),
                )

            }
        }

        drawPoints = visibleDataPointIdx.map {
            val xWidthes = (xLabelWidth + xLabelWidth / 2f)
            val x = viewPortLeftX + (it - visibleDataPointIdx.first) * xWidthes
            val ratio = (points[it].y - minYValue) / (maxYValue - minYValue)
            val y = viewPortBottomY - (ratio * viewportHeightPx)
            DrawPoint(
                x,
                y,
                points[it].xLabel
            )
        }

        val connectionsPoint1 = mutableListOf<DrawPoint>()
        val connectionsPoint2 = mutableListOf<DrawPoint>()
        for (i in 1 until drawPoints.size) {

            val p0 = drawPoints[i - 1]
            val p1 = drawPoints[i]
            val x = (p1.x + p0.x) / 2f
            val y1 = p0.y
            val y2 = p0.y

            connectionsPoint1.add(DrawPoint(x, y1, ""))
            connectionsPoint2.add(DrawPoint(x, y2, ""))
        }
        val linePath = Path().apply {
            if (drawPoints.isNotEmpty()) {
                moveTo(drawPoints.first().x, drawPoints.first().y)
                for (i in 1 until drawPoints.size) {
                    cubicTo(
                        x1 = connectionsPoint1[i - 1].x,
                        y1 = connectionsPoint1[i - 1].y,
                        x2 = connectionsPoint2[i - 1].x,
                        y2 = connectionsPoint2[i - 1].y,
                        x3 = drawPoints[i].x,
                        y3 = drawPoints[i].y
                    )
                }
            }
        }

        drawPath(
            linePath,
            color = style.chartLineColor,
            style = Stroke(
                width = 5f,
                cap = StrokeCap.Round
            )
        )
        drawPoints.forEachIndexed { index, drawPoint ->
            val selected = index == selectedPointIndex
            val color = if (selected) style.selectedColor else style.unselectedColor

            if (isShowingSelectedData) {
                drawCircle(
                    radius = if (selected) 15f else 10f,
                    color = color,
                    center = Offset(drawPoint.x, drawPoint.y),
                )
            }
        }

    }

}


@Preview(
    showBackground = true
)
@Composable
fun PreviewChart() {
    CryptoTrackerTheme {
        val history = remember {
            (1..20).map {
                CoinPrice(
                    Random.nextFloat() * 1000.0,
                    ZonedDateTime.now().plusHours(it.toLong()),
                )
            }
        }
        val points = remember {
            history.map {
                DrawPoint(
                    x = it.dateTime.hour.toFloat(),
                    y = it.priceUsd.toFloat(),
                    xLabel = DateTimeFormatter.ofPattern("ha\nm/d").format(it.dateTime),
                )
            }
        }
        val style = ChartStyle(
            chartLineColor = Color.Black,
            unselectedColor = Color(0xFF7c7c7c),
            helperLinesThickness = 5f,
            axisLinesThickensPxs = 5f,
            labelFontSize = 14.sp,
            minYLabelDp = 25.dp,
            verticalPadding = 8.dp,
            horizontalPadding = 8.dp,
            xAxisLabelSpacing = 8.dp,
            selectedColor = Color.Green,
            verticalSpacing = 16.dp
        )
        LineChart(
            style = style,
            visibleDataPointIdx = 0..19,
            points = points,
            unit = "$",
            modifier = Modifier
                .width(700.dp)
                .height(300.dp)
                .background(Color.White),
            selectedPoint = points[0]
        )
    }
}

private fun getSelectedPointIndex(
    touchOffset: Float,
    triggerWidth: Float,
    points: List<DrawPoint>
): Int {

    val triggerRangerLeft = touchOffset - triggerWidth / 2f
    val triggerRangeRight = touchOffset + triggerWidth / 2f
    return points.indexOfFirst {
        it.x in triggerRangerLeft..triggerRangeRight
    }
}