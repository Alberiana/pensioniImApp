package com.example.pensioniim
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amplifyframework.ui.liveness.model.FaceLivenessDetectionException
import com.amplifyframework.ui.liveness.ui.LivenessColorScheme
import com.example.pensioniim.ui.theme.PensioniImTheme
import com.example.pensioniim.uiApp.FaceLivenessDetector
import com.example.pensioniim.util.hasCameraPermission

class FaceLivenessScreen:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PensioniImTheme (
                    colorScheme = LivenessColorScheme.default()){
                    FaceLivenessDetector(
                        sessionId = sessionId,
                        region = "us-east-1",
                        disableStartView = false,
                        onComplete = {
                            viewModel.fetchSessionResult(sessionId)
                            onChallengeComplete()
                        },
                        onError = {
                            if (it is FaceLivenessDetectionException.UserCancelledException) {
                                onBack()
                            } else {
                                viewModel.reportErrorResult(it)
                                onChallengeComplete()
                            }
                        }
            }
        }
    }
}
@Composable
fun FaceLivenessContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "This is the next screen!",
            color = Color.Green
        )
    }
}
@Preview(showBackground = true)
@Composable
fun FaceLivenessContentPreview() {
    PensioniImTheme {
        FaceLivenessContent()
    }
}


@Preview(showBackground = true)
@Composable
fun NextScreenContentPreview() {
    PensioniImTheme {
        FaceLivenessContent()
    }
}
