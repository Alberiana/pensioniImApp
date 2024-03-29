package com.example.pensioniim.uiApp


import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.whenStarted
import com.amplifyframework.annotations.InternalAmplifyApi
import com.amplifyframework.predictions.aws.models.ColorDisplayInformation
import com.example.pensioniim.camera.OnFreshnessColorDisplayed
import com.example.pensioniim.model.SceneType
import com.example.pensioniim.state.FreshnessState

@Composable
internal fun nextFrameMillis(): State<Long> {
    val millisState = remember { mutableStateOf(System.currentTimeMillis()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.whenStarted {
            while (true) {
                millisState.value = withFrameMillis {
                    System.currentTimeMillis()
                }
            }
        }
    }
    return millisState
}

@OptIn(InternalAmplifyApi::class)
@Composable
internal fun FreshnessChallenge(
    key: Any,
    modifier: Modifier,
    colors: List<ColorDisplayInformation>,
    onColorDisplayed: OnFreshnessColorDisplayed,
    onComplete: () -> Unit
) {
    val currentOnColorDisplayed by rememberUpdatedState(onColorDisplayed)
    val currentOnComplete by rememberUpdatedState(onComplete)

    val freshnessState = remember(key) {
        FreshnessState(colors, currentOnColorDisplayed, currentOnComplete)
    }

    if (freshnessState.playbackEnded) {
        return
    }

    val nextFrameMillis = nextFrameMillis()

    val currentFrame by remember(key) {
        derivedStateOf {
            freshnessState.nextFrame(nextFrameMillis.value)
        }
    }

    Canvas(modifier = modifier) {
        currentFrame?.let { frame ->
            when (frame.sceneType) {
                is SceneType.DownScroll -> {
                    // This is the point on the y axis where current color should be above and
                    // the previous color should be below
                    val yAxisCompletion = size.height * frame.sceneCompletionPercentage

                    // Draw current color to the top of screen based on completion
                    drawRect(
                        color = frame.currentColor,
                        size = Size(size.width, yAxisCompletion)
                    )

                    frame.previousColor?.let {
                        // Draw previous color to the bottom of the screen based on completion
                        drawRect(
                            color = frame.previousColor,
                            topLeft = Offset(0f, yAxisCompletion),
                            size = Size(size.width, size.height - yAxisCompletion.toInt())
                        )
                    }
                }
                is SceneType.Flat -> {
                    drawRect(frame.currentColor)
                }
            }
        }
    }
}
