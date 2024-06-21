package com.example.pensioniim

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.exceptions.invalidstate.SignedInException
import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.ui.liveness.model.FaceLivenessDetectionException
import com.example.pensioniim.backend.LivenessSampleBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        Log.d("MainViewModel", "Creating session: ${_fetchingSession.value}")
        viewModelScope.launch {
            try {
                val sessionId = withContext(Dispatchers.IO) { LivenessSampleBackend.createSession() }
                Log.d("MainViewModel", "Response from endpoint: $sessionId")
                _sessionId.value = sessionId
                onComplete(sessionId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error creating liveness session", e)
                _sessionId.value = null
                onComplete(null)
            } finally {
                _fetchingSession.value = false
            }
        }
    }

    fun fetchSessionResult(sessionId: String) {
        Log.i("MainViewModel", "fetchSessionResult entry")
        if (_resultData.value != null) {
            Log.i("MainViewModel", "Exiting early, result data already present.")
            return
        }
        Log.i("MainViewModel", "Fetching session result")
        _fetchingResult.value = true
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { LivenessSampleBackend.getLivenessSessionResults(sessionId) }
                val auditImageBytes = result.auditImages?.firstOrNull()
                val auditImage = auditImageBytes?.let { base64String ->
                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                }
                val resultData = result.isLive?.let {
                    ResultData(
                        sessionId,
                        isLive = it,
                        confidenceScore = result.confidenceScore,
                        referenceImage = auditImage,
                    )
                }
                Log.i("MainViewModel", "Result Data: $resultData")
                _resultData.value = resultData
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching session result", e)
                val results = ResultData(
                    sessionId,
                    error = FaceLivenessDetectionException(
                        e.message ?: "Error retrieving liveness results",
                        throwable = e
                    )
                )
                _resultData.value = results
            } finally {
                _fetchingResult.value = false
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

