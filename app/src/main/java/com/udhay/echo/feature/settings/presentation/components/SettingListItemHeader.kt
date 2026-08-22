package com.udhay.echo.feature.settings.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingListItemHeader(
    modifier: Modifier = Modifier,
    heading: String = " "
) {
    Text(
        modifier = Modifier.padding(vertical = 12.dp),
        text = heading,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold
        )

    )
}

@Preview(showBackground = true)
@Composable
private fun SettingListItemHeaderPreview() {
    SettingListItemHeader()
}
