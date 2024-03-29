package com.example.pensioniim.uiApp

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.example.pensioniim.util.findActivity

@SuppressLint("SourceLockedOrientationActivity")
@Composable
internal fun LockPortraitOrientation(content: @Composable (resetOrientation: () -> Unit) -> Unit) {
    val context = LocalContext.current
    val activity = context.findActivity() ?: return content {}
    val originalOrientation by rememberSaveable { mutableStateOf(activity.requestedOrientation) }
    SideEffect {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // wait until screen is rotated correctly
    if (activity.resources.configuration.orientation == ORIENTATION_PORTRAIT) {
        content {
            activity.requestedOrientation = originalOrientation
        }
    }
}
