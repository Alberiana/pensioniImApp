package com.example.pensioniim.backend


import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.kotlin.core.Amplify
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object LivenessSampleBackend {
    private val logger = Amplify.Logging.forNamespace("LivenessSampleBackend")

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun createSession(): String {
        val request = RestOptions.builder()
            .addPath("/liveness/create")
            .build()
        logger.verbose("Creating session...")

        val response = Amplify.API.post(request)
        logger.verbose("Session created. Response: $response")
        return response.data.asJSONObject()["sessionId"] as String
    }

    suspend fun getLivenessSessionResults(sessionId: String): LivenessSessionResult {
        val request = RestOptions.builder()
            .addPath("/liveness/$sessionId")
            .build()
        logger.verbose("Getting session results for session ID: $sessionId")

        val result = Amplify.API.get(request)
        logger.verbose("Session results received. Response: $result")
        return json.decodeFromString(result.data.asString())
    }
}
@Serializable
data class LivenessSessionResult(
    val confidenceScore:Float,
    val isLive: Boolean,
    val auditImageBytes:String
)