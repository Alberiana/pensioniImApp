package com.example.pensioniim.camera


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.core.content.ContextCompat
import com.example.pensioniim.util.rotationDegrees

@SuppressLint("ViewConstructor", "Recycle")
internal class PreviewTextureView(
    context: Context,
    renderer: com.example.pensioniim.camera.OpenGLRenderer
) : TextureView(context) {

    private var surface: Surface? = null

    init {
        surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                surface = Surface(surfaceTexture).also {
                    renderer.attachPreviewSurface(
                        it,
                        Size(width, height),
                        this@PreviewTextureView.display.rotationDegrees()
                    )
                }
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}

            override fun onSurfaceTextureSizeChanged(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                surface = Surface(surfaceTexture).also {
                    renderer.attachPreviewSurface(
                        it,
                        Size(width, height),
                        display.rotationDegrees()
                    )
                }
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                val surfaceToDestroy = surface
                surface = null
                renderer.detachPreviewSurface().addListener({
                    surfaceToDestroy?.release()
                    surfaceTexture.release()
                }, ContextCompat.getMainExecutor(context))
                return false
            }
        }
    }
}
