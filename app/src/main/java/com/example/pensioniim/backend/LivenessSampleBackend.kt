package com.example.pensioniim.backend


import android.util.Log
import aws.smithy.kotlin.runtime.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

object LivenessSampleBackend {

    suspend fun createSession(): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://r4rc89ieh7.execute-api.us-east-1.amazonaws.com/default/createSessionUsEast")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody = response.body.string()
                Log.i("response", "This is the response: $responseBody")

                val json = Json { ignoreUnknownKeys = true }  // Create a Json instance with configuration
                val sessionId = json.parseToJsonElement(responseBody ?: "")
                    .jsonObject["body"]?.jsonPrimitive?.content?.let {
                    json.parseToJsonElement(it).jsonObject["sessionId"]?.jsonPrimitive?.content
                }
                Log.i("sessionId", "This is sessionId: $sessionId")

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

    suspend fun getLivenessSessionResults(sessionId: String): LivenessSessionResult {
        Log.i("GET RESULTTT", "Fetching result for session ID: $sessionId")

        val client = OkHttpClient()
        val url = "https://bl5832ba07.execute-api.us-east-1.amazonaws.com/default/GetFaceLivenessSessionResults/$sessionId"
        Log.i("GET RESULTTT", "Request URL: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    val responseData = response.body?.string()
                    Log.i("GET RESULTTT", "Response code: ${response.code}")
                    Log.i("GET RESULTTT", "Response data: $responseData")

                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }

                    if (responseData.isNullOrEmpty()) {
                        throw IOException("Empty response body")
                    }

                    try {
                        val json = Json { ignoreUnknownKeys = true }
                        val result: LivenessSessionResult = json.decodeFromString(responseData)
                        Log.i("GET RESULTTT", "Decoded result: $result")
                        return@withContext result
                    } catch (e: Exception) {
                        Log.e("JSON Decode Error", "Error decoding JSON response: ${e.message}", e)
                        throw e
                    }
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
    }

    suspend fun getIdentificationDetails(identificationNumber: String): IdentificationDetails {
        val client = OkHttpClient()
        val url = "https://3kgj5ykf62.execute-api.us-east-1.amazonaws.com/default/storedImages/$identificationNumber"
        Log.i("GET IDENTIFICATION DETAILS", "Request URL: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    val responseData = response.body?.string()
                    Log.i("GET IDENTIFICATION DETAILS", "Response code: ${response.code}")
                    Log.i("GET IDENTIFICATION DETAILS", "Response data: $responseData")

                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }

                    if (responseData.isNullOrEmpty()) {
                        throw IOException("Empty response body")
                    }

                    try {
                        val json = Json { ignoreUnknownKeys = true }
                        val result: IdentificationDetails = json.decodeFromString(responseData)
                        Log.i("GET IDENTIFICATION DETAILS", "Decoded result: $result")
                        return@withContext result
                    } catch (e: Exception) {
                        Log.e("JSON Decode Error", "Error decoding JSON response: ${e.message}", e)
                        throw e
                    }
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
    }
}
@Serializable
data class LivenessSessionResult(
    val isLive: Boolean?,
    val confidenceScore: Double?,
    val referenceImageUrl: String?,
    val auditImages: List<String>?
)
@Serializable
data class IdentificationDetails(
    val name: String,
    val surname: String,
    val image_url: String
)