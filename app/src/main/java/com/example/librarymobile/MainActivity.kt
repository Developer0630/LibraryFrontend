package com.example.librarymobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.librarymobile.ui.navigation.AppNavGraph

import com.example.librarymobile.ui.theme.LibraryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibraryTheme {
                // Chạy hệ thống điều hướng
                AppNavGraph()
            }
        }
    }
}