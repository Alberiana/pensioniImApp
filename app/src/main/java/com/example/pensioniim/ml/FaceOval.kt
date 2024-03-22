package com.example.pensioniim.ml


import android.graphics.RectF
import com.amplifyframework.annotations.InternalAmplifyApi
import com.amplifyframework.predictions.aws.models.FaceTargetChallenge
import kotlin.math.max

internal object FaceOval {

    @OptIn(InternalAmplifyApi::class)
    fun createBoundingRect(
        ovalInfo: FaceTargetChallenge
    ): RectF {
        val left = ovalInfo.targetCenterX - (ovalInfo.targetWidth / 2)
        val top = ovalInfo.targetCenterY - (ovalInfo.targetHeight / 2)
        val right = left + ovalInfo.targetWidth
        val bottom = top + ovalInfo.targetHeight
        return RectF(left, top, right, bottom)
    }

    // Creates a new rectangle that is a mirror of the given rectangle
    fun convertMirroredRectangle(rectangle: RectF, fullViewWidth: Int): RectF {
        val newLeft = max(0f, fullViewWidth - 1 - rectangle.right)
        val newRight = fullViewWidth - 1 - rectangle.left
        val newTop = rectangle.top
        val newBottom = rectangle.bottom
        return RectF(newLeft, newTop, newRight, newBottom)
    }

    fun convertMirroredLandmark(
        landmark: FaceDetector.Landmark,
        fullViewWidth: Int
    ): FaceDetector.Landmark {
        val newX = max(0f, fullViewWidth - 1 - landmark.x)
        val newY = landmark.y
        return FaceDetector.Landmark(newX, newY)
    }
}
