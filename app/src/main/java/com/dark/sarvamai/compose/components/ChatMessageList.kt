package com.dark.sarvamai.compose.components

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.VolumeUp
import androidx.compose.material.icons.twotone.CopyAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sarvam_ai.sarvam_sdk.api.api_interfaces.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChatMessageList(
    messages: List<Message>,
    modifier: Modifier = Modifier,
    onSpeak: (String) -> Unit // TTS callback
) {
    val clipboardManager: Clipboard = LocalClipboard.current

    LazyColumn(
        modifier = modifier.padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(messages) { msg ->
            if (msg.role != "system") {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    RichText(
                        text = msg.content.trim(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = if (msg.role == "user") TextAlign.End else TextAlign.Start
                    )
                    if (msg.role != "user") {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    clipboardManager.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "text",
                                                AnnotatedString(msg.content)
                                            )
                                        )
                                    )
                                }
                            }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.TwoTone.CopyAll, contentDescription = "Copy")
                            }

                            IconButton(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    onSpeak(msg.content)
                                }
                            }, Modifier.size(20.dp)) {
                                Icon(
                                    Icons.AutoMirrored.TwoTone.VolumeUp,
                                    contentDescription = "Speak"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
