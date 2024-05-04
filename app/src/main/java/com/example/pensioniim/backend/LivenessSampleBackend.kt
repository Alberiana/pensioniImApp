package com.example.pensioniim.backend


import android.util.Log
import aws.smithy.kotlin.runtime.io.IOException
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.kotlin.core.Amplify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

object LivenessSampleBackend {
    private val json = Json { ignoreUnknownKeys = true }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    suspend fun createSession(): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://uagj9wix4f.execute-api.eu-central-1.amazonaws.com/deployment/createSessionId-dev")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody = response.body?.string()
                Log.i("response", "This is the response: $responseBody")

                val sessionId = json.parseToJsonElement(
                    responseBody ?: ""
                ).jsonObject["sessionId"]?.jsonPrimitive?.content

                return sessionId ?: ""
            }
        } catch (e: IOException) {
            Log.e("IOException", "Error during HTTP call: ${e.message}", e)
            throw e
        } catch (e: Exception) {
            Log.e("Exception", "General error: ${e.message}", e)
            e.printStackTrace()
            throw e
        }
    }

    fun launchCreateSession(onResult: (String?) -> Unit, onError: (String) -> Unit) {
        scope.launch {
            try {
                val session = createSession()
                onResult(session)  // Invoke the callback with the session ID
            } catch (e: IOException) {
                Log.e("IOException", "Error during network call: ${e.message}", e)
                onError("Network error: ${e.message}")  // Send error message to the UI
            } catch (e: Exception) {
                Log.e("Exception", "Unexpected error: ${e.message}", e)
                onError("Unexpected error occurred")  // Send a generic error message
            }
        }
    }

    suspend fun getLivenessSessionResults(sessionId: String): LivenessSessionResult {
        val request = RestOptions.builder()
            .addPath("/liveness/$sessionId")
            .build()
        val result = Amplify.API.get(request)
        return json.decodeFromString(result.data.asString())
    }
}
@Serializable
data class LivenessSessionResult(
    val confidenceScore:Float,
    val isLive: Boolean,
    val auditImageBytes:String
)