package com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.chart

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tickerwatch.domain.repository.model.IntervalRangeValidator
import com.example.tickerwatch.domain.repository.model.Range
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType

@Composable
internal fun PeriodSelector(
    displayedRange: Range,
    onRangeChange: (Range) -> Unit
) {
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density
    val screenWidth = (context.resources.displayMetrics.widthPixels / density).dp

    val horizontalPadding = AppDimens.Space16
    val buttonCount = IntervalRangeValidator.allRanges.size
    val buttonWidth = (screenWidth - horizontalPadding * 2) / buttonCount
    val selectedIndex = IntervalRangeValidator.allRanges.indexOf(displayedRange)

    val indicatorOffset by animateDpAsState(
        targetValue = buttonWidth * selectedIndex,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "period_indicator"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = AppDimens.Space12)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IntervalRangeValidator.allRanges.forEachIndexed { index, range ->
                PeriodButton(
                    modifier = Modifier
                        .width(buttonWidth)
                        .height(AppDimens.ChartPeriodButtonHeight),
                    isSelected = range == displayedRange,
                    text = range.value,
                    onClick = { onRangeChange(range) }
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(indicatorOffset + (buttonWidth - AppDimens.ChartUnderlineWidth) / 2)
                .width(AppDimens.ChartUnderlineWidth)
                .height(AppDimens.ChartUnderlineHeight)
                .background(
                    AppColors.Accent,
                    shape = RoundedCornerShape(AppDimens.CornerChartUnderline)
                )
        )
    }

}
@Composable
private fun PeriodButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    text: String = "1D",
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = if (isSelected) AppColors.Accent else AppColors.Secondary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = AppType.Body
        )
    }
}

@Preview
@Composable
private fun PeriodSelectorPreview() {
    Box(modifier = Modifier.fillMaxWidth().background(AppColors.Surface)) {
    PeriodSelector(displayedRange = Range.ONE_DAY, onRangeChange = {}) }
}


