package com.example.pensioniim.uiApp

import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.example.pensioniim.util.findActivity

@Composable
internal fun AlwaysOnMaxBrightnessScreen() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalBrightness = activity.window.attributes.screenBrightness
        activity.window.addFlags(FLAG_KEEP_SCREEN_ON)
        activity.window.attributes = activity.window.attributes.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }
        onDispose {
            activity.window.clearFlags(FLAG_KEEP_SCREEN_ON)
            activity.window.attributes = activity.window.attributes.apply {
                screenBrightness = originalBrightness
            }
        }
    }
}