package com.example.kotlin_app.common.tickers

import androidx.annotation.DrawableRes
import com.example.kotlin_app.R
sealed interface Ticker {
    val symbol: String
    val logoRes: Int?
    val urlLogo: String?
}

enum class Sector(){
    TECHNOLOGY,
    HEALTHCARE,
    FINANCE
}

enum class CryptoEnum( override val symbol: String,
                         @DrawableRes override val logoRes: Int? = null,
                         override val urlLogo: String? = null): Ticker {
    BITCOIN("BTC-USD", null, "https://assets.coingecko.com/coins/images/1/large/bitcoin.png"),
    ETHEREUM("ETH-USD",null, "https://assets.coingecko.com/coins/images/279/large/ethereum.png");
}

enum class InvalidTicker(override val symbol: String, override val logoRes: Int? = null, override val urlLogo: String? = null): Ticker {
    INVALIDTICKER("INVALIDTICKER");
}

enum class StockMarketEnum( override val symbol: String,
                            @DrawableRes override val logoRes: Int? = null,
                            override val urlLogo: String? = null,
                            val sector: Sector? = null): Ticker {
    APPLE("AAPL", R.drawable.aapl, sector = Sector.TECHNOLOGY),
    MICROSOFT("MSFT", R.drawable.msft, sector = Sector.TECHNOLOGY),
    AMAZON("AMZN", R.drawable.amzn, sector = Sector.TECHNOLOGY),
    ALPHABET("GOOGL", R.drawable.googl, sector = Sector.TECHNOLOGY),
    META("META", R.drawable.meta, sector = Sector.TECHNOLOGY),
    NVIDIA("NVDA", R.drawable.nvda, sector = Sector.TECHNOLOGY),
    TESLA("TSLA", R.drawable.tsla, sector = Sector.TECHNOLOGY),
    AMD("AMD", R.drawable.amd, sector = Sector.TECHNOLOGY),
    INTEL("INTC", R.drawable.intc, sector = Sector.TECHNOLOGY),
    IBM("IBM", R.drawable.ibm, sector = Sector.TECHNOLOGY),
    VISA("V", R.drawable.v, sector = Sector.FINANCE),
    MASTERCARD("MA", R.drawable.ma, sector = Sector.FINANCE),
    JPMORGAN("JPM", R.drawable.jpm, sector = Sector.FINANCE),
    BANK_OF_AMERICA("BAC", R.drawable.bac, sector = Sector.FINANCE),
    GOLDMAN_SACHS("GS", R.drawable.gs, sector = Sector.FINANCE),
}

object TickerRegistry {
        val allStockMarketTickers = StockMarketEnum.entries
        val allCryptoTickers = CryptoEnum.entries
    
       fun  retrieveAllTickers(): List<Ticker> = allStockMarketTickers + allCryptoTickers

        fun replaceSymbolWithTickerEnum(symbol: String): Ticker  {
            val firstResult = StockMarketEnum.entries.toTypedArray().find { it.symbol == symbol} ?: InvalidTicker.INVALIDTICKER
            if (firstResult != InvalidTicker.INVALIDTICKER)
                return firstResult
            return  CryptoEnum.entries.toTypedArray().find { it.symbol == symbol} ?: InvalidTicker.INVALIDTICKER
        }
}
