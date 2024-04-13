package com.example.pensioniim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "main") {
                composable("main") {
                    MainActivityContent(navController)
                }
            }
        }
    }

    @Composable
    fun MainActivityContent(navController: NavHostController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to Your App!")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate("faceLivenessScreen")
            }) {
                Text("Start Face Liveness Detection")
            }
        }
    }

    @Composable
    fun FaceLivenessScreenContent(navController: NavHostController) {
        val viewModel: MainViewModel = viewModel()
        FaceLivenessScreen(viewModel = viewModel, onChallengeComplete = {
            navController.navigate("results") {
                popUpTo("challenge") {
                    inclusive = true
                }
            }
        }, onBack = {
            viewModel.clearSession()
            navController.popBackStack()
        })
    }
}


