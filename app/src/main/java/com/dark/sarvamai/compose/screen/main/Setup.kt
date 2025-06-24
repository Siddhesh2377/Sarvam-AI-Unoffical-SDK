package com.dark.sarvamai.compose.screen.main

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.dark.sarvamai.utils.UserPrefs
import com.sarvam_ai.sarvam_sdk.BuildConfig
import kotlinx.coroutines.launch

@Composable
fun SetupScreen(
    paddingValues: PaddingValues,
    onComplete: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var apiKey by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Sarvam AI",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it; showError = false },
            label = { Text("Enter your API Key") },
            isError = showError,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (apiKey.isBlank()) {
                        showError = true
                    } else {
                        scope.launch {
                            UserPrefs.setApiKey(context, apiKey)
                            onComplete()
                        }
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (showError) {
            Text(
                text = "API Key cannot be empty",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                focusManager.clearFocus()
                if (apiKey.isBlank()) showError = true
                else {
                    scope.launch {
                        UserPrefs.setApiKey(context, apiKey)
                        onComplete()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save & Continue")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(
            onClick = {
                scope.launch {
                    UserPrefs.setApiKey(context, BuildConfig.API_KEY)
                    onComplete()
                }
            }
        ) {
            Text("Skip for now")
        }
    }
}
