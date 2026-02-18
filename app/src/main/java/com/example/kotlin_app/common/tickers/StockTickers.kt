package com.example.kotlin_app.common.tickers

import androidx.annotation.DrawableRes
import com.example.kotlin_app.R
sealed interface Ticker {
    val tickerName: String
    val symbol: String
    val logoRes: Int?
    val urlLogo: String?
}

enum class Sector(){
    TECHNOLOGY,
    HEALTHCARE,
    FINANCE
}

enum class CryptoEnum(override val tickerName: String,
                      override val symbol: String,
                      @DrawableRes override val logoRes: Int? = null,
                      override val urlLogo: String? = null): Ticker {
    BITCOIN("Bitcoin", "BTC-USD", null, "https://assets.coingecko.com/coins/images/1/large/bitcoin.png"),
    ETHEREUM("Ethereum", "ETH-USD", null, "https://assets.coingecko.com/coins/images/279/large/ethereum.png");
}

enum class InvalidTicker(override val tickerName: String = "Invalid",
                         override val symbol: String,
                         override val logoRes: Int? = null,
                         override val urlLogo: String? = null): Ticker {
    INVALIDTICKER("Invalid", "INVALIDTICKER");
}

enum class StockMarketEnum(override val tickerName: String,
                           override val symbol: String,
                           @DrawableRes override val logoRes: Int? = null,
                           override val urlLogo: String? = null,
                           val sector: Sector? = null): Ticker {
    APPLE("Apple", "AAPL", R.drawable.aapl, null, Sector.TECHNOLOGY),
    MICROSOFT("Microsoft", "MSFT", R.drawable.msft, null, Sector.TECHNOLOGY),
    AMAZON("Amazon", "AMZN", R.drawable.amzn, null, Sector.TECHNOLOGY),
    ALPHABET("Alphabet", "GOOGL", R.drawable.googl, null, Sector.TECHNOLOGY),
    META("Meta", "META", R.drawable.meta, null, Sector.TECHNOLOGY),
    NVIDIA("NVIDIA", "NVDA", R.drawable.nvda, null, Sector.TECHNOLOGY),
    TESLA("Tesla", "TSLA", R.drawable.tsla, null, Sector.TECHNOLOGY),
    AMD("AMD", "AMD", R.drawable.amd, null, Sector.TECHNOLOGY),
    INTEL("Intel", "INTC", R.drawable.intc, null, Sector.TECHNOLOGY),
    IBM("IBM", "IBM", R.drawable.ibm, null, Sector.TECHNOLOGY),
    VISA("Visa", "V", R.drawable.v, null, Sector.FINANCE),
    MASTERCARD("Mastercard", "MA", R.drawable.ma, null, Sector.FINANCE),
    JPMORGAN("JPMorgan", "JPM", R.drawable.jpm, null, Sector.FINANCE),
    BANK_OF_AMERICA("Bank of America", "BAC", R.drawable.bac, null, Sector.FINANCE),
    GOLDMAN_SACHS("Goldman Sachs", "GS", R.drawable.gs, null, Sector.FINANCE),
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