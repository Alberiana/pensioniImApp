package com.example.pensioniim.backend

import com.amplifyframework.api.rest.RestOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.amplifyframework.kotlin.core.Amplify
object LivenessBackend{

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun createSession(): String {
        val request = RestOptions.builder()
            .addPath("/liveness/create")
            .build()

        return Amplify.API.post(request).data.asJSONObject()["sessionId"] as String
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
    val confidenceScore: Float,
    val isLive: Boolean,
    val auditImageBytes: String
)