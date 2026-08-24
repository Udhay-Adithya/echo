package com.udhay.echo.feature.chat.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.udhay.echo.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDetailToolTip(
    modifier: Modifier = Modifier,
    richTooltipText: String = "N/A"
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val coroutineScope = rememberCoroutineScope()
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Above,
            spacingBetweenTooltipAndAnchor = 12.dp
        ),
        tooltip = {
            RichTooltip(
                title = { Text("Details") }
            ) {
                Text(richTooltipText)
            }
        },
        state = tooltipState
    ) {
        IconButton(onClick = {
            coroutineScope.launch {
                if(tooltipState.isVisible){
                    tooltipState.dismiss()
                }
                else{
                    tooltipState.show()
                }

            }
        }) {
            Icon(
                painter = painterResource(R.drawable.info_24px),
                contentDescription = "Show more information"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ModelDetailToolTipPreview() {
    ModelDetailToolTip()
}
