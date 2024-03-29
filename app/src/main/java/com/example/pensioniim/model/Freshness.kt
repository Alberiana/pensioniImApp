package com.example.pensioniim.model


import androidx.compose.ui.graphics.Color
import com.amplifyframework.annotations.InternalAmplifyApi
import com.amplifyframework.predictions.aws.models.ColorDisplayInformation
import com.amplifyframework.predictions.aws.models.RgbColor

@OptIn(InternalAmplifyApi::class)
internal fun RgbColor.toComposeColor(alpha: Int) =
    Color(
        red = red,
        green = green,
        blue = blue,
        alpha = alpha
    )

internal sealed class SceneType {
    object DownScroll : SceneType()
    object Flat : SceneType()
}

internal data class FreshnessColorScene @OptIn(InternalAmplifyApi::class) constructor(
    val startTime: Long,
    val endTime: Long,
    val currentColor: ColorDisplayInformation,
    val previousColor: ColorDisplayInformation?,
    val sceneType: SceneType
)

internal data class FreshnessColorFrame(
    val sceneType: SceneType,
    val currentColor: Color,
    val previousColor: Color?,
    val sceneCompletionPercentage: Float
)
