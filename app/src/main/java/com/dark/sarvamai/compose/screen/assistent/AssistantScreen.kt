package com.dark.sarvamai.compose.screen.assistent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.GraphicEq
import androidx.compose.material.icons.twotone.Mic
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dark.sarvamai.compose.components.RichText
import com.dark.sarvamai.viewmodel.ConversionViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AssistantScreen(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: ConversionViewModel = viewModel()
) {
    val context = LocalContext.current
    val isRecording by viewModel.isRecording.collectAsState()
    val message by viewModel.message.collectAsState()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), contentAlignment = Alignment.Center
        ) {
            AnimatedContent(isRecording) { it ->
                if (it) LoadingIndicator(Modifier.width(300.dp))
                else IdleCompose()
            }
        }

        Row(Modifier.padding(bottom = 24.dp)) {
            IconButton(onClick = { viewModel.toggleRecording(context) }) {
                AnimatedContent(isRecording, label = "") { it ->
                    Icon(
                        imageVector = if (it) Icons.TwoTone.GraphicEq else Icons.TwoTone.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(34.dp),
                        tint = if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxWidth( )
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .heightIn(max = 450.dp) // Limit max height
                .verticalScroll(scrollState) // Make scrollable when needed
        ) {
            RichText(
                text = message,
                modifier = Modifier.padding(bottom = 44.dp).padding(horizontal = 20.dp),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        }
    }
}


@Composable
fun IdleCompose(dotSize: Dp = 20.dp) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(5) { index ->
            AnimatedDot(dotSize = dotSize, delayMillis = index * 150)
        }
    }
}

@Composable
fun AnimatedDot(dotSize: Dp, delayMillis: Int) {
    var expanded by remember { mutableStateOf(false) }

    val animatedHeight by animateDpAsState(
        targetValue = if (expanded) dotSize * 2.8f else dotSize,
        animationSpec = tween(durationMillis = 600),
        label = "DotHeight"
    )

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        while (true) {
            expanded = !expanded
            delay(700)
        }
    }

    Box(
        modifier = Modifier
            .width(dotSize)
            .height(animatedHeight)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    )
}