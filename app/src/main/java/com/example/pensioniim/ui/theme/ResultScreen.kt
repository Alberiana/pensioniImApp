package com.example.pensioniim.ui.theme

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pensioniim.MainViewModel
import com.example.pensioniim.ResultData
import java.text.NumberFormat
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

@Composable
fun ResultScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    val fetchingSession by viewModel.fetchingResult.collectAsState()
    val resultData by viewModel.resultData.collectAsState()

    Log.d("ResultScreen", "Fetching session: $fetchingSession, Result data: $resultData")

    if (fetchingSession) {
        Log.d("ResultScreen", "Displaying loading indicator")
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
        }
    } else {
        resultData?.let { data ->
            Log.d("ResultScreen", "Displaying results view with data: $data")
            ResultsView(resultData = data)
        } ?: run {
            Log.d("ResultScreen", "No result data available")
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No result data available")
            }
        }
    }
}

@Composable
private fun ResultsView(resultData: ResultData) {
    Log.d("ResultsView", "Result data: $resultData")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Confidence Score",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        resultData.confidenceScore?.let { score ->
            Text(
                text = formattedConfidenceScore(score.toFloat()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        } ?: run {
            Text("Confidence score not available")
        }
    }
}

private fun formattedConfidenceScore(confidenceScore: Float): String {
    var truncatedConfidenceScore = floor(confidenceScore * 10000) / 10000
    truncatedConfidenceScore = min(truncatedConfidenceScore, 99.9999f)
    truncatedConfidenceScore = max(truncatedConfidenceScore, 0.0001f)

    return NumberFormat.getInstance().apply {
        maximumFractionDigits = 4
        minimumFractionDigits = 4
    }.format(truncatedConfidenceScore)
}
