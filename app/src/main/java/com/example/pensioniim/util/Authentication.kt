package com.example.pensioniim.util
import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
class Authentication: Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
        } catch (e: AmplifyException) {
            Log.e("AuthenticatorSampleApp", "Cannot instantiate Amplify. Please see the getting started guide at https://docs.amplify.aws/lib/auth/getting-started/q/platform/android/ for details.", e)
        }
    }
}
