package com.example.pensioniim


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.amplifyframework.ui.liveness.model.FaceLivenessDetectionException
import com.amplifyframework.ui.liveness.ui.FaceLivenessDetector
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceLivenessScreen(
    viewModel: MainViewModel,
    onChallengeComplete: () -> Unit,
    onBack: () -> Unit
) {
    Log.d("FaceLivenessScreen", "sessionId startingggg")

    val sessionIdState = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(true) {
        viewModel.createLivenessSession { sessionId ->
            sessionIdState.value = sessionId
        }
    }
    Log.d("FaceLivenessScreen hereeee", "sessionIdState.value: ${sessionIdState.value}")

    sessionIdState.value?.let { sessionId ->
        MaterialTheme {
            BackHandler(onBack = onBack)

            FaceLivenessDetector(
                sessionId = sessionId,
                region = "us-east-1",
                disableStartView = false,
                onComplete = {
                    viewModel.fetchSessionResult(sessionId)
                    onChallengeComplete()
                },
                onError = { error ->
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
}
