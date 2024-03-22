package com.example.pensioniim.model


import android.graphics.RectF
import com.amplifyframework.ui.liveness.R
import com.example.pensioniim.ml.FaceDetector

internal sealed class LivenessCheckState(val instructionId: Int? = null, val isActionable: Boolean = true) {
    class Initial(
        instructionId: Int? = null,
        isActionable: Boolean = true
    ) : LivenessCheckState(instructionId, isActionable) {
        companion object {
            fun withMoveFaceMessage() =
                Initial(R.string.amplify_ui_liveness_challenge_instruction_move_face)
            fun withMultipleFaceMessage() =
                Initial(R.string.amplify_ui_liveness_challenge_instruction_multiple_faces_detected)
            fun withMoveFaceFurtherAwayMessage() =
                Initial(R.string.amplify_ui_liveness_challenge_instruction_move_face_further)
            fun withConnectingMessage() =
                Initial(R.string.amplify_ui_liveness_challenge_connecting, false)
            fun withStartViewMessage() =
                Initial(R.string.amplify_ui_liveness_get_ready_center_face_label)
        }
    }
    class Running(instructionId: Int? = null) : LivenessCheckState(instructionId, true) {
        companion object {
            fun withMoveFaceMessage() = Running(
                R.string.amplify_ui_liveness_challenge_instruction_move_face_closer
            )
            fun withMultipleFaceMessage() = Running(
                R.string.amplify_ui_liveness_challenge_instruction_multiple_faces_detected
            )
            fun withFaceOvalPosition(faceOvalPosition: FaceDetector.FaceOvalPosition) =
                Running(faceOvalPosition.instructionStringRes)
        }
    }
    object Error : LivenessCheckState(isActionable = false)
    class Success(val faceGuideRect: RectF) :
        LivenessCheckState(R.string.amplify_ui_liveness_challenge_verifying, false)
}
