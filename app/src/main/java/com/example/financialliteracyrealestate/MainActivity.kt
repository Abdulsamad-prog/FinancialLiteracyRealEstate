package com.example.financialliteracyrealestate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.financialliteracyrealestate.ui.navigation.AppNavigation
import com.example.financialliteracyrealestate.ui.theme.FinancialLiteracyRealEstateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialLiteracyRealEstateTheme {
                AppNavigation()
            }
        }
    }
}