package com.example.pensioniim.uiApp

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import com.amplifyframework.ui.liveness.ui.LivenessColorScheme

@Composable
internal fun LivenessPreviewContainer(
    colorScheme: ColorScheme = LivenessColorScheme.default(),
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = MaterialTheme.typography,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = shapes,
        typography = typography
    ) {
        content()
    }
}
