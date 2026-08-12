package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.OrderScreen
import com.example.ui.OrderViewModel
import com.example.ui.theme.OrderTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrderTrackerTheme {
                val viewModel: OrderViewModel = viewModel()
                OrderScreen(viewModel = viewModel)
            }
        }
    }
}
