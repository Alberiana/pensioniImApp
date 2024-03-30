package com.example.pensioniim
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.example.pensioniim.FaceLivenessScreen
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize Amplify
        Amplify.addPlugin(AWSCognitoAuthPlugin())
        // Add other plugins as needed
        Amplify.configure(applicationContext)
        val faceLivenessScreen = findViewById<Button>(R.id.btnNext)
        faceLivenessScreen.setOnClickListener{
            Log.d("MainActivity", "Button clicked") // Log statement
            val intent = Intent(this, FaceLivenessScreen::class.java)
            startActivity(intent)
        }
    }
}

