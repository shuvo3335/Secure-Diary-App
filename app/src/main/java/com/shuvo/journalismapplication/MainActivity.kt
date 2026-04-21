package com.shuvo.journalismapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.shuvo.journalismapplication.ui.navigation.JournalNavHost
import com.shuvo.journalismapplication.ui.theme.JournalismApplicationTheme

import androidx.lifecycle.viewmodel.compose.viewModel
import com.shuvo.journalismapplication.ui.viewmodel.JournalViewModel
import com.shuvo.journalismapplication.ui.viewmodel.JournalViewModelFactory

import androidx.fragment.app.FragmentActivity
import com.shuvo.journalismapplication.utils.BiometricHelper
import androidx.compose.runtime.*

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = (application as JournalismApp).database
        val dao = database.journalEntryDao()
        
        setContent {
            JournalismApplicationTheme {
                var isAuthenticated by remember { mutableStateOf(false) }
                var authError by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    if (BiometricHelper.isBiometricAvailable(this@MainActivity)) {
                        BiometricHelper.showBiometricPrompt(
                            activity = this@MainActivity,
                            onSuccess = { isAuthenticated = true },
                            onError = { error -> authError = error }
                        )
                    } else {
                        // If biometric is not set up, just let them in or handle accordingly
                        isAuthenticated = true
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isAuthenticated) {
                        val viewModel: JournalViewModel = viewModel(
                            factory = JournalViewModelFactory(dao)
                        )
                        val navController = rememberNavController()
                        JournalNavHost(navController = navController, viewModel = viewModel)
                    } else if (authError != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.Text(
                                text = "Authentication failed: $authError. Please restart the app.",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
