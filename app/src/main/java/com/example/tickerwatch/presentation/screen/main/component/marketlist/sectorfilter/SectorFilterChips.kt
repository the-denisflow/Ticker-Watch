package com.example.tickerwatch.presentation.screen.main.component.marketlist.sectorfilter

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tickerwatch.presentation.theme.AppColors
import com.example.tickerwatch.presentation.theme.AppDimens
import com.example.tickerwatch.presentation.theme.AppType


enum class SectorFilter(val label: String){
    ALL("All"),
    TECH("Tech"),
    FINANCE("Finance"),
    HEALTH("Health"),
    CRYPTO("Crypto"),
}

@Composable
internal fun SectorFilterChips(
    activeFilter: SectorFilter,
    onFilterSelected: (SectorFilter)-> Unit
) {
    Row(
        modifier = Modifier. fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = AppDimens.Space16, vertical = AppDimens.Space8),
            horizontalArrangement = Arrangement.spacedBy(AppDimens.Space8)
    ) {
        SectorFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == activeFilter,
                onClick = { onFilterSelected(filter) },
                label = { Text(text = filter.label, fontSize = AppType.Body) },
                shape = RoundedCornerShape(AppDimens.CornerPill),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = AppColors.SurfaceVariant,
                    labelColor = AppColors.Secondary,
                    selectedContainerColor = AppColors.Primary,
                    selectedLabelColor = AppColors.Surface
                ),
                border = null
            )
        }
    }
}

@Preview
@Composable
private fun SectorFilterChipsPreview() {
    SectorFilterChips(
        activeFilter = SectorFilter.ALL,
        onFilterSelected = {}
    )
}