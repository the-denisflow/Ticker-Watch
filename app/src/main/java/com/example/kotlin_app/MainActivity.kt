package com.example.kotlin_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.kotlin_app.common.Logger
import com.example.kotlin_app.presentation.fragments.StockFragment
import com.example.kotlin_app.presentation.fragments.StockListFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logger.info("[onCreate]")
        supportFragmentManager.beginTransaction()
            .replace(R.id.stock_fragment_container, StockListFragment())
            .commit()
        }
}