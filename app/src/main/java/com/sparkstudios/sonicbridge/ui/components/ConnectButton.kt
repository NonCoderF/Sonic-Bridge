package com.sparkstudios.sonicbridge.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ConnectButton(
    isConnected: Boolean,
    onClick: () -> Unit
) {

    val transition =
        rememberInfiniteTransition(label = "")

    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (isConnected) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(170.dp)
                .scale(pulse)
                .background(
                    color = if (isConnected)
                        Color(0xFF00C853)
                    else
                        Color(0xFF00BCD4),
                    shape = CircleShape
                )
                .border(
                    4.dp,
                    Color.White.copy(alpha = .25f),
                    CircleShape
                )
                .clickable {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {

            AnimatedContent(
                targetState = isConnected,
                label = ""
            ) { connected ->

                if (connected) {

                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )

                } else {

                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )

                }

            }

        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text =
            if (isConnected)
                "CONNECTED"
            else
                "CONNECT",

            style =
            MaterialTheme.typography.titleLarge,

            color =
            if (isConnected)
                Color(0xFF00E676)
            else
                Color.White
        )

    }

}