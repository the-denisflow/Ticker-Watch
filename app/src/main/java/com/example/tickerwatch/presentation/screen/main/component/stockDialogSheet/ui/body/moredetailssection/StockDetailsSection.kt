package com.example.tickerwatch.presentation.screen.main.component.stockDialogSheet.ui.body.moredetailssection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.tickerwatch.presentation.model.StockDetailRow
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType

@Composable
internal fun StockDetailsSection(
    detailsRow: List<StockDetailRow>
) {
    if (detailsRow.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.Space20, vertical = AppDimens.Space8)
            .background(AppColors.SurfaceVariant, shape = RoundedCornerShape(AppDimens.CornerCard))
            .padding(horizontal = AppDimens.Space16)
    ) {
        detailsRow.forEachIndexed { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppDimens.Space12),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = row.label,
                    fontSize = AppType.BodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = AppColors.Secondary
                )
                Text(
                    text = row.value,
                    fontSize = AppType.BodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Primary
                )
            }
            if (index < detailsRow.lastIndex) {
                HorizontalDivider(
                    thickness = AppDimens.DividerThickness,
                    color = AppColors.DividerSubtle
                )
            }
        }
    }
}