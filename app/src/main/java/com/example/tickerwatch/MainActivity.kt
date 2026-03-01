package com.example.tickerwatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.example.tickerwatch.common.Logger
import com.example.tickerwatch.presentation.fragments.StockListFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure status bar icons are dark so they are visible on the white background
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        setContentView(R.layout.activity_main)
        logger.info("[onCreate]")
        supportFragmentManager.beginTransaction()
            .replace(R.id.stock_fragment_container, StockListFragment())
            .commit()
    }
}