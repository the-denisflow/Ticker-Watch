package com.example.kotlin_app.presentation.ui.components.homepagelist.composeable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoadingState (message: String = "Fetching latest stock data..") {
    Box(Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
       Column (horizontalAlignment = Alignment.CenterHorizontally) {
           CircularProgressIndicator()
           Spacer(Modifier.height(12.dp))
           Text(message)
       }

    }
}

@Preview
@Composable
fun LoadingStatePreview() {
    LoadingState()
}