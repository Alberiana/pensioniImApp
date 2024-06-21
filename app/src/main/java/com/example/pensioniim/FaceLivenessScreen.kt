package com.example.pensioniim


import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.amplifyframework.ui.liveness.model.FaceLivenessDetectionException
import com.amplifyframework.ui.liveness.ui.FaceLivenessDetector
import com.amplifyframework.ui.liveness.ui.LivenessColorScheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceLivenessScreen(
    viewModel: MainViewModel,
    onChallengeComplete: () -> Unit,
    onBack: () -> Unit
) {
    Log.d("FaceLivenessScreen", "Starting Face Liveness Screen")

    val sessionIdState = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(true) {
        viewModel.createLivenessSession { sessionId ->
            if (sessionId != null) {
                Log.d("FaceLivenessScreen", "Session created successfully: $sessionId")
                sessionIdState.value = sessionId
            } else {
                Log.e("FaceLivenessScreen", "Failed to create session")
            }
        }
    }

    sessionIdState.value?.let { sessionId ->
        Log.d("FaceLivenessScreen", "sessionIdState.value: $sessionId")

        MaterialTheme(colorScheme = LivenessColorScheme.default()) {
            FaceLivenessDetector(
                sessionId = sessionId,
                region = "us-east-1",
                disableStartView = false,
                onComplete = {
                    Log.d("FaceLivenessScreen", "Fetching session result")
                    viewModel.fetchSessionResult(sessionId)
                    onChallengeComplete()
                },
                onError = { exception ->
                    Log.e("FaceLivenessScreen", "Face liveness detection error: $exception")
                    when (exception) {
                        is FaceLivenessDetectionException.AccessDeniedException -> {
                            Log.e("FaceLivenessScreen", "Access denied: ${exception.message}")
                        }
                        is FaceLivenessDetectionException.UserCancelledException -> {
                            Log.d("FaceLivenessScreen", "User cancelled face liveness detection")
                            onBack()
                        }
                        else -> {
                            Log.e("FaceLivenessScreen", "Error during face liveness detection: ${exception.message}")
                        }
                    }
                    viewModel.reportErrorResult(exception)
                    onChallengeComplete()
                }
            )
        }
    }
}
