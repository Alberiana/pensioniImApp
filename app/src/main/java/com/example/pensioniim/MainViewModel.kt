package com.example.pensioniim


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

class MainViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Fetching)
    val authState = _authState.asStateFlow()

    private val _fetchingSession = MutableLiveData<Boolean>()
    val fetchingSession: LiveData<Boolean> get() = _fetchingSession

    private val _sessionId = MutableLiveData<String?>()
    val sessionId: LiveData<String?> get() = _sessionId

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
          //  Log.e(MainActivity.TAG, "fetchAuthState failed", error)
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
                    //Log.e(MainActivity.TAG, "Failed to sign in", e)
                    AuthState.SignedOut
                }
            }
        }
    }

    fun createLivenessSession(onComplete: (sessionId: String?) -> Unit) {
        _fetchingSession.postValue(true)
        Log.d("CreateLivenessSession", "Creating session")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sessionId = LivenessSampleBackend.createSession()
                Log.d("CreateLivenessSession", "Response from endpoint: $sessionId")

                _sessionId.postValue(sessionId)
                onComplete(sessionId)
            } catch (e: Exception) {
                Log.e("CreateLivenessSession", "Error creating liveness session", e)
                _sessionId.postValue(null)
                onComplete(null)
            } finally {
                _fetchingSession.postValue(false)
            }
        }
    }

    fun fetchSessionResult(sessionId: String) {
        if (_resultData.value != null) return //we already have result, likely timeout

        _fetchingResult.value = true
        viewModelScope.launch {
            try {
                val result = LivenessSampleBackend.getLivenessSessionResults(sessionId)

                val imageBytes = Base64.decode(result.auditImageBytes, Base64.DEFAULT)
                val auditImage = BitmapFactory.decodeByteArray(
                    imageBytes,
                    0,
                    imageBytes.size
                )

                val resultData = ResultData(
                    sessionId,
                    isLive = result.isLive,
                    confidenceScore = result.confidenceScore,
                    referenceImage = auditImage,
                )

                _resultData.value = resultData
            } catch (e: Exception) {
                val results = ResultData(
                    sessionId,
                    error = FaceLivenessDetectionException(
                        e.message ?: "Error retrieving liveness results",
                        throwable = e
                    )
                )
                _resultData.value = results
            }
            _fetchingResult.value = false
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
    val confidenceScore: Float = 0f,
    val referenceImage: Bitmap? = null,
    val error: FaceLivenessDetectionException? = null,
)