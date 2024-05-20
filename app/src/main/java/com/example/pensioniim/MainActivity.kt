package com.example.pensioniim

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pensioniim.ui.theme.ResultScreen

class MainActivity : ComponentActivity() {
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private var navigateToFaceLiveness by mutableStateOf(false)

    private companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            navigateToFaceLiveness = isGranted
        }
        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "main") {
                composable("main") {
                    MainActivityContent(navController)
                }
                composable("faceLivenessScreen") {
                    FaceLivenessScreenContent(navController)
                }
                composable("ResultScreen") { // Make sure this route is exactly "ResultScreen"
                    val viewModel: MainViewModel = viewModel()
                    ResultScreen(viewModel = viewModel, onBack = { navController.popBackStack() })                }
            }


        }
    }

    @Composable
    fun MainActivityContent(navController: NavHostController) {
        val localNavController = rememberNavController()
        if (navigateToFaceLiveness) {
            LaunchedEffect(Unit) {
                localNavController.navigate("faceLivenessScreen")
                navigateToFaceLiveness = false  // Reset the navigation trigger
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to Your App!")
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (checkCameraPermission()) {
                    navController.navigate("faceLivenessScreen")
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
                Text("Start Face Liveness Detection")
            }

        }
    }
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    @Composable
    fun FaceLivenessScreenContent(navController: NavHostController) {
        val viewModel: MainViewModel = viewModel()
        FaceLivenessScreen(viewModel = viewModel, onChallengeComplete = {
            navController.navigate("ResultScreen") {
                popUpTo("challenge") {
                    inclusive = true
                }
            }
        }, onBack = {
            viewModel.clearSession()
            navController.popBackStack()
        })
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ResultScreenContent(navController: NavHostController) {
        // Content and logic of the ResultScreen
        Scaffold(
            topBar = { TopAppBar(title = { Text("Result") }) },
            content = { paddingValues ->
                ResultScreenBody(navController, paddingValues)
            }
        )
    }
    @Composable
    fun ResultScreenBody(navController: NavHostController, paddingValues: PaddingValues) {
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
          //  Text("Face Liveness Check Complete", style = MaterialTheme3.typography.h5)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController.navigate("main") }) {
                Text("Back to Main")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Perform Another Check")
            }
        }
    }



}


