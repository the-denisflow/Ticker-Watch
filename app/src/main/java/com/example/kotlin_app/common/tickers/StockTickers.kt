package com.example.kotlin_app.common.tickers

import androidx.annotation.DrawableRes
import com.example.kotlin_app.R

sealed interface Ticker {
    val symbol: String
    val logoRes: Int?
    val urlLogo: String?
}



enum class StockTicker( override val symbol: String,
                         @DrawableRes override val logoRes: Int? = null,
                         override val urlLogo: String? = null): Ticker {
    IVALIDTICKER("INVALIDTICKER"),
    APPLE("AAPL", R.drawable.aapl),
    MICROSOFT("MSFT", R.drawable.msft),
    AMAZON("AMZN", R.drawable.amzn),
    ALPHABET("GOOGL", R.drawable.googl),
    META("META", R.drawable.meta),
    NVIDIA("NVDA", R.drawable.nvda),
    TESLA("TSLA", R.drawable.tsla),
    AMD("AMD", R.drawable.amd),
    INTEL("INTC", R.drawable.intc),
    IBM("IBM", R.drawable.ibm),
    VISA("V", R.drawable.v),
    MASTERCARD("MA", R.drawable.ma),
    JPMORGAN("JPM", R.drawable.jpm),
    BANK_OF_AMERICA("BAC", R.drawable.bac),
    GOLDMAN_SACHS("GS", R.drawable.gs),

    // Cryptos
    BITCOIN("BTC-USD", null, "https://assets.coingecko.com/coins/images/1/large/bitcoin.png"),
    ETHEREUM("ETH-USD",null, "https://assets.coingecko.com/coins/images/279/large/ethereum.png");

    companion object {
        val allTickers = values().toList().filterNot { it == IVALIDTICKER }

        fun toStockTicker(symbol: String): StockTicker = entries.toTypedArray().find { it.symbol == symbol} ?: IVALIDTICKER
    }
}
