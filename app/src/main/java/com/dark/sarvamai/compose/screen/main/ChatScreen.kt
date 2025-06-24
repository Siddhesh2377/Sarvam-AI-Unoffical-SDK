package com.dark.sarvamai.compose.screen.main

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.twotone.GraphicEq
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dark.sarvamai.activity.AssistantActivity
import com.dark.sarvamai.compose.components.ChatMessageList
import com.dark.sarvamai.utils.UserPrefs.getApiKey
import com.dark.sarvamai.viewmodel.ChatViewModel
import com.sarvam_ai.sarvam_sdk.api.chat.ChatClient.init
import com.sarvam_ai.sarvam_sdk.api.stt.STTClient
import com.sarvam_ai.sarvam_sdk.api.tts.TTSClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChatScreen(paddingValues: PaddingValues, viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            init(getApiKey(context).toString())
            TTSClient.init(getApiKey(context).toString())
            STTClient.init(getApiKey(context).toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .imePadding()
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Sarvam AI",
                style = MaterialTheme.typography.displaySmall,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(24.dp)
            )

            Spacer(Modifier.weight(1f))

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(24.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Icon(
                    imageVector = Icons.TwoTone.Settings,
                    contentDescription = "Settings"
                )
            }
        }

        ChatMessageList(
            messages, modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) { text ->
            CoroutineScope(Dispatchers.IO).launch {
                TTSClient.generate(text, onCompletion = {
                    TTSClient.playAudio(it, context)
                }, onError = {
                    it.printStackTrace()
                })
            }
        }

        OutlinedCard(
            modifier = Modifier
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 16.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            vertical = 24.dp, horizontal = 8.dp
                        ),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    decorationBox = { innerTextField ->
                        if (input.isEmpty()) {
                            Text(
                                "Say Anything...",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                )

                var record by remember { mutableStateOf(false) }

                IconButton(
                    onClick = {
                        record = !record
                        CoroutineScope(Dispatchers.IO).launch {
                            if (record) {
                                STTClient.startRecording(context.cacheDir.resolve("temp.wav"))
                            } else {
                                STTClient.stopRecording()
                                STTClient.transcribe(
                                    context.cacheDir.resolve("temp.wav"),
                                    onCompletion = {
                                        input = it
                                        Log.d("ChatScreen", "Transcribed: $it")
                                    },
                                    onError = {
                                        it.printStackTrace()
                                    }
                                )
                            }
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    AnimatedContent(record, label = "record") {
                        when (it) {
                            true -> {
                                LoadingIndicator()
                            }

                            false -> {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    contentDescription = "Settings"
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            viewModel.sendMessage(input) { output ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    //        TTSClient.fetchAndPlayTTS(output)
                                    input = ""
                                }
                            }
                            input = ""
                        }else{
                            context.startActivity(Intent(context, AssistantActivity::class.java))
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Crossfade(input.isEmpty()) {
                        when (it) {
                            true -> Icon(
                                imageVector = Icons.TwoTone.GraphicEq,
                                contentDescription = "Settings"
                            )

                            false -> Icon(
                                imageVector = Icons.AutoMirrored.TwoTone.Send,
                                contentDescription = "Settings"
                            )
                        }
                    }
                }
            }
        }
    }
}