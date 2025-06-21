package com.dark.sarvamai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dark.sarvamai.compose.screen.ChatScreen
import com.dark.sarvamai.compose.screen.SetupScreen
import com.dark.sarvamai.ui.theme.SarvamAiTheme
import com.dark.sarvamai.utils.UserPrefs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SarvamAiTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var isSetupComplete by remember { mutableStateOf(true) }

                    LaunchedEffect(Unit) {
                        isSetupComplete = UserPrefs.getApiKey(this@MainActivity).toString() != "none"
                    }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                    ) { innerPadding ->
                        AnimatedContent(targetState = isSetupComplete) { completed ->
                            if (!completed) {
                                SetupScreen(innerPadding) {
                                    isSetupComplete = true
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
}
