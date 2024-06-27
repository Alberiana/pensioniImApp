package com.example.pensioniim

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.exceptions.invalidstate.SignedInException
import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.ui.liveness.model.FaceLivenessDetectionException
import com.example.pensioniim.backend.IdentificationDetails
import com.example.pensioniim.backend.LivenessSampleBackend
import com.example.pensioniim.backend.LivenessSampleBackend.getIdentificationDetails
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.net.HttpURLConnection
import java.net.URL

class MainViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Fetching)
    val authState = _authState.asStateFlow()

    private val _fetchingSession = MutableStateFlow(false)
    val fetchingSession = _fetchingSession.asStateFlow()

    private val _sessionId = MutableStateFlow<String?>(null)
    val sessionId = _sessionId.asStateFlow()

    private val _fetchingResult = MutableStateFlow(false)
    val fetchingResult = _fetchingResult.asStateFlow()

    private val _resultData = MutableStateFlow<ResultData?>(null)
    val resultData = _resultData.asStateFlow()

    var identificationDetails by mutableStateOf<IdentificationDetails?>(null)

    init {
        viewModelScope.launch {
            fetchAuthState()
        }
    }

    private suspend fun fetchAuthState() {
        _authState.value = AuthState.Fetching
        _authState.value = try {
            val result = Amplify.Auth.fetchAuthSession()
            if (result.isSignedIn) {
                AuthState.SignedIn
            } else {
                AuthState.SignedOut
            }
        } catch (error: AuthException) {
            Log.e("MainViewModel", "fetchAuthState failed", error)
            AuthState.SignedOut
        }
    }

    fun launchSignIn(activity: Activity) {
        _authState.value = AuthState.SigningIn
        viewModelScope.launch {
            _authState.value = try {
                Amplify.Auth.signInWithWebUI(activity)
                AuthState.SignedIn
            } catch (e: Exception) {
                if (e is SignedInException) {
                    AuthState.SignedIn
                } else {
                    Log.e("MainViewModel", "Failed to sign in", e)
                    AuthState.SignedOut
                }
            }
        }
    }

    fun createLivenessSession(onComplete: (sessionId: String?) -> Unit) {
        _fetchingSession.value = true
        Log.d("createLivenessSession", "Creating session: ${_fetchingSession.value}")
        viewModelScope.launch {
            try {
                val sessionId = withContext(Dispatchers.IO) { LivenessSampleBackend.createSession() }
                Log.d("createLivenessSession", "Response from endpoint: $sessionId")
                _sessionId.value = sessionId
                onComplete(sessionId)
            } catch (e: Exception) {
                Log.e("createLivenessSession", "Error creating liveness session", e)
                _sessionId.value = null
                onComplete(null)
            } finally {
                _fetchingSession.value = false
            }
        }
    }



    fun fetchSessionResult(sessionId: String) {
        if (_resultData.value != null) return // If results already exist, skip fetching
        _fetchingResult.value = true

        viewModelScope.launch {
            try {
                val result = withTimeout(10000) { // 10 seconds timeout
                    LivenessSampleBackend.getLivenessSessionResults(sessionId)
                }

                val resultData = result.isLive?.let {
                    ResultData(
                        sessionId = sessionId,
                        isLive = it,
                        confidenceScore = result.confidenceScore,
                        referenceImage = null,
                        error = null
                    )
                }

                Log.d("fetchSessionResult", "Result data: $resultData")

                _resultData.value = resultData
                Log.d("fetchSessionResult", "_resultData.value: ${_resultData.value}")

            } catch (e: TimeoutCancellationException) {
                Log.e("fetchSessionResult", "Timeout fetching session result for sessionId: $sessionId", e)
            } catch (e: CancellationException) {
                Log.w("fetchSessionResult", "Job was cancelled for sessionId: $sessionId", e)
            } catch (e: Exception) {
                Log.e("fetchSessionResult", "Error fetching session result for sessionId: $sessionId", e)
                val results = ResultData(
                    sessionId = sessionId,
                    isLive = true,
                    confidenceScore = null,
                    referenceImage = null,
                    error = FaceLivenessDetectionException(
                        e.message ?: "Error retrieving liveness results",
                        throwable = e
                    )
                )
                _resultData.value = results
            } finally {
                Log.i("fetchSessionResult", "Setting fetchingResult to false for sessionId: $sessionId")
                _fetchingResult.value = false
            }
        }
    }



    suspend fun downloadImage(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                Log.e("downloadImage", "Error downloading image from URL: $imageUrl", e)
                null
            }
        }
    }
    fun fetchIdentificationDetails(identificationNumber: String) {
        viewModelScope.launch {
            try {
                identificationDetails = getIdentificationDetails(identificationNumber)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching identification details: ${e.message}", e)
            }
        }
    }
    fun reportErrorResult(exception: FaceLivenessDetectionException) {
        sessionId.value?.let {
            _resultData.value = ResultData(it, error = exception)
            _fetchingResult.value = false
        }
    }

    fun clearSession() {
        _sessionId.value = null
        _resultData.value = null
        _fetchingResult.value = false
        _fetchingSession.value = false
    }
}

sealed class AuthState {
    object Fetching : AuthState()
    object SignedIn : AuthState()
    object SigningIn : AuthState()
    object SignedOut : AuthState()
}

data class ResultData(
    val sessionId: String,
    val isLive: Boolean = false,
    val confidenceScore: Double? = 0.0,
    val referenceImage: Bitmap? = null,
    val error: FaceLivenessDetectionException? = null,
)

