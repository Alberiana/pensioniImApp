package com.example.pensioniim



import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.amplifyframework.ui.liveness.model.FaceLivenessDetectionException
import com.amplifyframework.ui.liveness.ui.FaceLivenessDetector
import com.amplifyframework.ui.liveness.ui.LivenessColorScheme

@Composable
fun FaceLivenessScreen(
    viewModel: MainViewModel,
    onChallengeComplete: () -> Unit,
    onBack: () -> Unit
) {
    Log.d("TRYINGGG BackHandler ", "BackHandler TO Starting FaceLivenessDetector")

    BackHandler(onBack = onBack)
    Log.d("TRYINGGG BEFOREE SESSION", "TO Starting FaceLivenessDetector")

    val sessionId = viewModel.sessionId.collectAsState().value ?: return

    MaterialTheme(colorScheme = LivenessColorScheme.default()) {
        Log.d("TRYINGGG", "Starting FaceLivenessDetector with sessionId: $sessionId")
        FaceLivenessDetector(
            sessionId = sessionId,
            region = "us-east-1",
            disableStartView = false,
            onComplete = {
                Log.d("FaceLivenessScreen", "FaceLivenessDetector onComplete")
                viewModel.fetchSessionResult(sessionId)
                onChallengeComplete()
            },
            onError = { error ->
                Log.e("FaceLivenessScreen", "FaceLivenessDetector onError: ${error.message}")
                if (error is FaceLivenessDetectionException.UserCancelledException) {
                    onBack()
                } else {
                    viewModel.reportErrorResult(error)
                    onChallengeComplete()
                }

            }

        )
    }
}