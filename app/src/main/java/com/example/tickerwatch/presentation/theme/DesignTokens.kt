package com.example.tickerwatch.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// Colors
// ---------------------------------------------------------------------------
object AppColors {

    // Text hierarchy
    val Primary       = Color(0xFF1C1C1E)   // titles, prices, main content
    val Secondary     = Color(0xFF8E8E93)   // subtitles, captions, neutral trend
    val Tertiary      = Color(0xFFBEBEC0)   // inactive icons, placeholders
    val Quaternary    = Color(0xFFAEAEB2)   // sub-labels (e.g. "from yesterday")
    val Strong        = Color.Black         // emphasis (e.g. current price row)
    val ChipContent   = Color(0xFF3C3C43)   // text inside metadata chips

    // Surfaces
    val Surface        = Color.White
    val SurfaceVariant = Color(0xFFF2F2F7)  // chip/filter backgrounds, cards

    // Dividers
    val Divider       = Color(0xFFEEEEEE)   // list row separators
    val DividerSubtle = Color(0xFFE5E5EA)   // detail dialog inner dividers

    // Trend (price movement)
    val TrendUp          = Color(0xFF2E7D32)
    val TrendUpSurface   = Color(0xFFE8F5E9)
    val TrendDown        = Color(0xFFC62828)
    val TrendDownSurface = Color(0xFFFFEBEE)

    // Sectors
    val SectorTech          = Color(0xFF1565C0)
    val SectorTechSurface   = Color(0xFFE3F2FD)
    val SectorFinance       = Color(0xFF2E7D32)
    val SectorFinanceSurface= Color(0xFFE8F5E9)
    val SectorHealth        = Color(0xFFE65100)
    val SectorHealthSurface = Color(0xFFFFF3E0)
    val SectorCrypto        = Color(0xFF6A1B9A)
    val SectorCryptoSurface = Color(0xFFF3E5F5)

    // Brand
    val Accent = Color(0xFF007AFF)   // chart period underline, selected period text
    val Scrim  = Color.Black.copy(alpha = 0.3f)

    // Portfolio gradient — gain (green) and loss (red)
    val PortfolioGainDark  = Color(0xFF1A6B3C)
    val PortfolioGainLight = Color(0xFF2E7D32)
    val PortfolioLossDark  = Color(0xFF9B1B1B)
    val PortfolioLossLight = Color(0xFFC62828)

    // Text rendered on top of a colored gradient card
    val OnGradient        = Color.White
    val OnGradientMuted   = Color.White.copy(alpha = 0.85f)
    val OnGradientCaption = Color.White.copy(alpha = 0.7f)
    val OnGradientDisabled= Color.White.copy(alpha = 0.5f)
    val OnGradientOverlay = Color.White.copy(alpha = 0.2f)
}

// ---------------------------------------------------------------------------
// Dimensions
// ---------------------------------------------------------------------------
object AppDimens {

    // Spacing scale (use these for padding / arrangement gaps)
    val Space1  = 1.dp
    val Space2  = 2.dp
    val Space3  = 3.dp
    val Space4  = 4.dp
    val Space5  = 5.dp
    val Space6  = 6.dp
    val Space7  = 7.dp
    val Space8  = 8.dp
    val Space10 = 10.dp
    val Space12 = 12.dp
    val Space14 = 14.dp
    val Space16 = 16.dp
    val Space20 = 20.dp
    val Space24 = 24.dp
    val Space32 = 32.dp

    // Icon sizes
    val IconXs       = 16.dp   // small UI icons (sort button, filter icon)
    val IconSm       = 20.dp   // bookmark icon inside button, sort-option check
    val IconMd       = 28.dp   // watchlist card remove button area
    val IconLg       = 36.dp   // watchlist card logo, empty-state icon
    val IconXl       = 40.dp   // holding row logo, sort-option icon box, bookmark btn
    val IconCheck    = 22.dp   // check indicator in sort sheet
    val IconStockRow = 42.dp   // StockInfoRow default logo size
    val IconDialog   = 48.dp   // stock-detail dialog header logo

    // Component sizes
    val StockRowHeight    = 72.dp   // height of a single stock list row
    val SparklineWidth    = 72.dp   // mini sparkline in stock row
    val SparklineHeight   = 32.dp
    val EmptyStateIconBox = 72.dp   // circular container for empty-state icon

    // Corner radii
    val CornerXs           = 4.dp   // sector chip
    val CornerSm           = 6.dp   // trend badge
    val CornerMd           = 8.dp   // portfolio badge, sort-option icon
    val CornerLg           = 10.dp  // sort-option icon box
    val CornerCard         = 12.dp  // details section, sort sheet
    val CornerWatchlistCard= 16.dp  // watchlist grid card
    val CornerPill         = 20.dp  // filter chips, portfolio summary, sort button
    val CornerModal        = 24.dp  // bottom sheets
    val CornerChartUnderline = 1.dp // animated period underline

    // Lines & strokes
    val DividerThickness   = 0.5.dp
    val SparklineStroke    = 1.5.dp
    val ChartUnderlineHeight = 2.dp
    val ChartUnderlineWidth  = 28.dp
    val CardShadow           = 4.dp

    // Drag-and-drop (watchlist grid)
    val DragScale           = 1.05f
    val DragAlpha           = 0.92f
    val NonDragAlpha        = 0.7f
    val DragShadowElevation = 16f

    // Chart controls
    val ChartPeriodButtonHeight = 36.dp
}

// ---------------------------------------------------------------------------
// Typography (font sizes only — weights remain inline with FontWeight.*)
// ---------------------------------------------------------------------------
object AppType {
    val SectorChip  =  9.sp   // label inside sector chip
    val NavLabel    = 10.sp   // bottom-navigation tab label
    val Badge       = 11.sp   // trend badge, change percentage
    val Caption     = 12.sp   // symbol, secondary info, metadata chips
    val Body        = 13.sp   // filter chips, sort descriptions, period buttons
    val BodyMedium  = 14.sp   // detail rows, empty-state description
    val BodyLarge   = 15.sp   // stock name in list, prices, holding name
    val CardTitle   = 16.sp   // watchlist card price
    val SectionTitle= 18.sp   // section headers, sort sheet title, dialog perf row
    val PageTitle   = 28.sp   // "Markets" / "Portfolio" page headers
    val DisplayLarge= 32.sp   // portfolio total value
}