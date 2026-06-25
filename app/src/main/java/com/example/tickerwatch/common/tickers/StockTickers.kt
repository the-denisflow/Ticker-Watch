package com.example.tickerwatch.common.tickers

import androidx.annotation.DrawableRes
import com.example.tickerwatch.R

sealed class LogoResource {
    data class Res(@DrawableRes val resId: Int): LogoResource()
    data class Url(val url: String): LogoResource()
}

sealed interface Ticker {
    val tickerName: String
    val symbol: String
    val logo: LogoResource?
}

enum class Sector {
    TECHNOLOGY,
    HEALTHCARE,
    FINANCE
}

enum class Country(val displayName: String, val flag: String) {
    UNITED_STATES("United States", "🇺🇸"),
    UNITED_KINGDOM("United Kingdom", "🇬🇧"),
    GERMANY("Germany", "🇩🇪"),
    FRANCE("France", "🇫🇷"),
    JAPAN("Japan", "🇯🇵"),
    CHINA("China", "🇨🇳"),
}

enum class CryptoEnum(override val tickerName: String,
                      override val symbol: String,
                      override val logo: LogoResource? = null): Ticker {
    BITCOIN("Bitcoin", "BTC-USD", LogoResource.Url(url = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png")),
    ETHEREUM("Ethereum", "ETH-USD", LogoResource.Url("https://assets.coingecko.com/coins/images/279/large/ethereum.png"));
}

enum class InvalidTicker(override val tickerName: String = "Invalid",
                         override val symbol: String,
                         override val logo: LogoResource? = null): Ticker {
    INVALIDTICKER("Invalid", "INVALIDTICKER");
}

enum class StockMarketEnum(override val tickerName: String,
                           override val symbol: String,
                           override val logo: LogoResource? = null,
                           val sector: Sector? = null,
                           val country: Country? = null): Ticker {
    APPLE("Apple", "AAPL", LogoResource.Res(R.drawable.aapl), Sector.TECHNOLOGY, Country.UNITED_STATES),
    MICROSOFT("Microsoft", "MSFT", LogoResource.Res(R.drawable.msft), Sector.TECHNOLOGY, Country.UNITED_STATES),
    AMAZON("Amazon", "AMZN", LogoResource.Res(R.drawable.amzn), Sector.TECHNOLOGY, Country.UNITED_STATES),
    ALPHABET("Alphabet", "GOOGL", LogoResource.Res(R.drawable.googl), Sector.TECHNOLOGY, Country.UNITED_STATES),
    META("Meta", "META", LogoResource.Res(R.drawable.meta), Sector.TECHNOLOGY, Country.UNITED_STATES),
    NVIDIA("NVIDIA", "NVDA", LogoResource.Res(R.drawable.nvda), Sector.TECHNOLOGY, Country.UNITED_STATES),
    TESLA("Tesla", "TSLA", LogoResource.Res(R.drawable.tsla), Sector.TECHNOLOGY, Country.UNITED_STATES),
    AMD("AMD", "AMD", LogoResource.Res(R.drawable.amd), Sector.TECHNOLOGY, Country.UNITED_STATES),
    INTEL("Intel", "INTC", LogoResource.Res(R.drawable.intc), Sector.TECHNOLOGY, Country.UNITED_STATES),
    IBM("IBM", "IBM", LogoResource.Res(R.drawable.ibm), Sector.TECHNOLOGY, Country.UNITED_STATES),
    VISA("Visa", "V", LogoResource.Res(R.drawable.v), Sector.FINANCE, Country.UNITED_STATES),
    MASTERCARD("Mastercard", "MA", LogoResource.Res(R.drawable.ma), Sector.FINANCE, Country.UNITED_STATES),
    JPMORGAN("JPMorgan", "JPM", LogoResource.Res(R.drawable.jpm), Sector.FINANCE, Country.UNITED_STATES),
    BANK_OF_AMERICA("Bank of America", "BAC", LogoResource.Res(R.drawable.bac), Sector.FINANCE, Country.UNITED_STATES),
    GOLDMAN_SACHS("Goldman Sachs", "GS", LogoResource.Res(R.drawable.gs), Sector.FINANCE, Country.UNITED_STATES),
}

object TickerRegistry {
    val allStockMarketTickers = StockMarketEnum.entries
    val allCryptoTickers = CryptoEnum.entries

    fun retrieveAllTickers(): List<Ticker> = allStockMarketTickers + allCryptoTickers

    fun replaceSymbolWithTickerEnum(symbol: String): Ticker {
        val firstResult = StockMarketEnum.entries.toTypedArray().find { it.symbol == symbol} ?: InvalidTicker.INVALIDTICKER
        if (firstResult != InvalidTicker.INVALIDTICKER)
            return firstResult
        return CryptoEnum.entries.toTypedArray().find { it.symbol == symbol} ?: InvalidTicker.INVALIDTICKER
    }
}

fun Ticker.getTagsFromTicker(): List<String> = when (this) {
    is StockMarketEnum -> listOfNotNull(
        this.sector?.name?.lowercase()?.replaceFirstChar { it.titlecase() },
        this.country?.let { "${it.flag} ${it.displayName}" }
    )
    is CryptoEnum -> listOf("Crypto")
    else -> emptyList()
}
