package org.yankauskas.pstest.presentation.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.yankauskas.pstest.presentation.compose.exchange.ExchangeScreen
import org.yankauskas.pstest.presentation.compose.theme.TestComposeTheme
import org.yankauskas.pstest.presentation.exchange.ExchangeViewModel

class ComposeMainActivity : ComponentActivity() {

    private val viewModel: ExchangeViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            TestComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ExchangeScreen(
                        viewModel, modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}