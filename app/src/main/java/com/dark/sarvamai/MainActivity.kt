package com.dark.sarvamai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.dark.sarvamai.compose.screen.ChatScreen
import com.dark.sarvamai.compose.screen.SetupScreen
import com.dark.sarvamai.ui.theme.SarvamAiTheme
import com.dark.sarvamai.utils.UserPrefs
import com.sarvam_ai.sarvam_sdk.api.chat.ChatClient
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val context = this

        lifecycleScope.launch {
            val apiKey = UserPrefs.getApiKey(context)// This suspends until value is read
            ChatClient.init(apiKey)

            setContent {
                SarvamAiTheme {
                    var isSetupDone by remember { mutableStateOf(apiKey != "none") }

                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        if (!isSetupDone) {
                            SetupScreen(innerPadding) {
                                isSetupDone = true
                            }
                        } else {
                            ChatScreen(innerPadding)
                        }
                    }
                }
            }
        }
    }
}
