package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
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
            MindLoopTheme {
                MindLoopApp(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
