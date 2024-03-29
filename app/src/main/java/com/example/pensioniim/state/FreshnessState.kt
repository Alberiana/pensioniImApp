package com.example.pensioniim.state

import com.amplifyframework.annotations.InternalAmplifyApi
import com.amplifyframework.predictions.aws.models.ColorDisplayInformation
import com.amplifyframework.predictions.aws.models.RgbColor
import com.example.pensioniim.model.FreshnessColorFrame
import com.example.pensioniim.model.FreshnessColorScene
import com.example.pensioniim.model.SceneType
import com.example.pensioniim.model.toComposeColor

@OptIn(InternalAmplifyApi::class)
internal typealias OnColorDisplayed = (
    currentColor: RgbColor,
    previousColor: RgbColor,
    sequenceNumber: Int,
    colorStartTime: Long
) -> Unit

@OptIn(InternalAmplifyApi::class)
internal data class FreshnessState @OptIn(InternalAmplifyApi::class) constructor(
    val freshnessColors: List<ColorDisplayInformation>,
    val onColorDisplayed: OnColorDisplayed,
    val onComplete: () -> Unit
) {

    private val freshnessColorScript: List<FreshnessColorScene>

    private var playbackStarted = -1L
    private var currentSceneIndex = 0
    private var lastDisplayedSceneIndex = -1 // used to track onColorDisplayed callback
    var playbackEnded = false

    @OptIn(InternalAmplifyApi::class)
    fun nextFrame(currentTime: Long): FreshnessColorFrame? {
        if (playbackEnded) return null

        if (playbackStarted == -1L) {
            // start playback if not yet started
            playbackStarted = currentTime
        }

        // get current relative start time for player
        val playbackTime = currentTime - playbackStarted

        while (playbackTime > freshnessColorScript[currentSceneIndex].endTime) {
            // increase scene index until finding non-expired scene
            currentSceneIndex += 1

            // End Playback if all colors have expired
            if (currentSceneIndex >= freshnessColors.size) {
                playbackEnded = true
                onComplete()
                return null
            }
        }

        return freshnessColorScript[currentSceneIndex].let {
            val scenePlaybackTime = playbackTime - it.startTime
            val sceneDuration = it.endTime - it.startTime
            val sceneCompletionPercentage = scenePlaybackTime.toFloat() / sceneDuration

            val currentFreshnessColor = it.currentColor.color
            val previousFreshnessColor = it.previousColor?.color

            if (lastDisplayedSceneIndex != currentSceneIndex) {
                onColorDisplayed(
                    currentFreshnessColor,
                    previousFreshnessColor ?: currentFreshnessColor,
                    currentSceneIndex,
                    currentTime
                )
                lastDisplayedSceneIndex = currentSceneIndex
            }

            FreshnessColorFrame(
                sceneType = it.sceneType,
                currentColor = currentFreshnessColor.toComposeColor(
                    if (currentSceneIndex == 0) FIRST_SCENE_ALPHA else REMAINING_SCENE_ALPHA
                ),
                previousColor = previousFreshnessColor?.toComposeColor(
                    if (currentSceneIndex == 1) FIRST_SCENE_ALPHA else REMAINING_SCENE_ALPHA
                ),
                sceneCompletionPercentage = if (it.sceneType is SceneType.DownScroll) {
                    sceneCompletionPercentage
                } else {
                    100f
                }
            )
        }
    }

    init {
        var accumulator = 0L
        freshnessColorScript = freshnessColors.mapIndexed { index, color ->
            val sceneType = if (color.shouldScroll) SceneType.DownScroll else SceneType.Flat
            val startTime = accumulator
            val duration = color.duration.toLong()
            val endTime = startTime + duration
            FreshnessColorScene(
                startTime = startTime,
                endTime = endTime,
                currentColor = color,
                previousColor = freshnessColors.getOrNull(index - 1),
                sceneType = sceneType
            ).also {
                accumulator = endTime
            }
        }
    }

    companion object {
        const val FIRST_SCENE_ALPHA = (255 * .90).toInt()
        const val REMAINING_SCENE_ALPHA = (255f * .75).toInt()
    }
}
