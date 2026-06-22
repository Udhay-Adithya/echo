package com.udhay.kollama.feature.chat.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udhay.kollama.core.ui.common.Loader

@Composable
fun AssistantLoaderBubble() {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val bubbleMaxWidth = maxWidth * 0.75f

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
                modifier = Modifier.widthIn(max = bubbleMaxWidth)
            ) {
                Loader(
                    fill = false,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}
