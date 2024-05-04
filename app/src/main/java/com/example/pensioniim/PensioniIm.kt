package com.example.pensioniim

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify

class PensioniIm  : Application() {

    override fun onCreate() {
        super.onCreate()
        //xxx
        try {
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)

            Log.i("GOOOOOODDDDDD", "Initialized Amplify")
            Log.d("GOOOOOODDDDDD", "Initialized Amplify")

        } catch (error: AmplifyException) {
            Log.e("BADDDDDDD", "Could not initialize Amplify", error)
        }
    }

}