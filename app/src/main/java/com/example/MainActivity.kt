package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.firestore.FirestoreRepository
import com.example.ui.MindLoopApp
import com.example.ui.theme.MindLoopTheme
import com.example.ui.viewmodel.MindLoopViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MindLoopViewModel by viewModels {
        val repository = FirestoreRepository(applicationContext)
        MindLoopViewModel.provideFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val systemInDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> systemInDark
            }

            MindLoopTheme(darkTheme = isDark) {
                MindLoopApp(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
