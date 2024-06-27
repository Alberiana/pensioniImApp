package com.example.pensioniim

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                navigateToFaceLiveness = isGranted
            }

        setContent {
            val navController = rememberNavController()
            val viewModel: MainViewModel = viewModel()

            NavHost(navController, startDestination = "main") {
                composable("main") {
                    MainActivityContent(navController, viewModel)
                }
                composable("faceLivenessScreen") {
                    FaceLivenessScreenContent(navController, viewModel)
                }
                composable("ResultScreen") {
                    Log.d("MainActivity", "Navigating to ResultScreen with ViewModel: $viewModel")
                    ResultScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                }
                composable("IDCardScreen") {
                    IDCardScreen(navController)
                }
                composable("ContinueVerificationScreen/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    ContinueVerificationScreen(navController, viewModel, id)
                }
            }
        }
    }


    @Composable
    fun MainActivityContent(navController: NavHostController, viewModel: MainViewModel) {
        val localNavController = rememberNavController()
        if (navigateToFaceLiveness) {
            LaunchedEffect(Unit) {
                localNavController.navigate("faceLivenessScreen")
                navigateToFaceLiveness = false  // Reset the navigation trigger
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.size(280.dp))
            Image(
                painter = painterResource(id = R.drawable.pensioni),
                contentDescription = null
            )
            Text(
                text = "Mirësevini në aplikacionin ku mund te verfikoheni per pensionin tuaj!",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp),
                style = TextStyle(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    navController.navigate("IDCardScreen")

                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .width(300.dp)
                    .height(60.dp)
            )
            {
                Text("Vazhdo")
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
    fun FaceLivenessScreenContent(navController: NavHostController, viewModel: MainViewModel) {
        FaceLivenessScreen(viewModel = viewModel, onChallengeComplete = {
            navController.navigate("ResultScreen") {
                popUpTo("faceLivenessScreen") {
                    inclusive = true
                }
            }
        }, onBack = {
            viewModel.clearSession()
            navController.popBackStack()
        })
    }

    @Composable
    fun ResultScreenBody(navController: NavHostController, paddingValues: PaddingValues, viewModel: MainViewModel) {
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate("main") }) {
                Text("Back to Main")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Perform Another Check")
            }
        }
    }


    @Composable
    fun IDCardScreen(navController: NavHostController) {
        var text by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.size(300.dp))
            Text(
                text = "Ju lutem jepni numrin personal të letërnjoftimit: ",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            )
            OutlinedTextField(
                value = text,
                shape = RoundedCornerShape(12.dp),
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        text = it
                        isError = it.length != 10
                    }
                },
                label = {
                    Text(
                        text = if (isError) "Numri i ID duhet të jetë saktësisht 10 shifra" else "ID e letërnjoftimit",
                        style = TextStyle(color = if (isError) Color.Red else Color.Gray)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    if (text.length == 10) {
                        Log.d("IDCardScreen", "Navigating to ContinueVerificationScreen with ID: $text")
                        navController.navigate("ContinueVerificationScreen/$text")
                    } else {
                        Log.d("IDCardScreen", "ID length is not 10, current length: ${text.length}")
                        isError = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .width(300.dp)
                    .height(60.dp)
            ) {
                Text("Vazhdo")
            }
        }
    }



    @Composable
    fun ContinueVerificationScreen(navController: NavHostController, viewModel: MainViewModel, id: String) {
        var isError by remember { mutableStateOf(false) }

        val identificationDetails by viewModel::identificationDetails

        LaunchedEffect(id) {
            viewModel.fetchIdentificationDetails(id)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.size(300.dp))
            Text(
                text = if (identificationDetails != null) "Mirëseerdhe ${identificationDetails!!.name} ${identificationDetails!!.surname}!" else "Duke kërkuar informacion...",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp),
                style = TextStyle(
                    fontSize = if (identificationDetails != null) 38.sp else 24.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    if (identificationDetails != null) {
                        if (checkCameraPermission()) {
                            navController.navigate("faceLivenessScreen")
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .width(300.dp)
                    .height(60.dp)
            ) {
                Text("Verifikohu me fytyrë")
            }
        }
    }




    @Preview
    @Composable
    fun previewMainActivity() {
        val navController = rememberNavController()
        val viewModel: MainViewModel = viewModel()
        MainActivityContent(navController = navController, viewModel = viewModel)
    }
}







