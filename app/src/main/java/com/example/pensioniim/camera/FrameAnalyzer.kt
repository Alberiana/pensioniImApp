package com.example.pensioniim.camera


import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.pensioniim.ml.FaceDetector
import com.example.pensioniim.ml.FaceOval
import com.example.pensioniim.state.LivenessState
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.image.ImageProcessor


internal class FrameAnalyzer(
    context: Context,
    private val livenessState: LivenessState
) : ImageAnalysis.Analyzer {

    private val tfLite = FaceDetector.loadModel(context)
    private val tfImageBuffer = TensorImage(DataType.UINT8)
    private var tfImageProcessor: ImageProcessor? = null

    private var cachedBitmap: Bitmap? = null
    private var faceDetector = FaceDetector(livenessState)

    override fun analyze(image: ImageProxy) {
        if (cachedBitmap == null) {
            cachedBitmap = Bitmap.createBitmap(
                image.width,
                image.height,
                Bitmap.Config.ARGB_8888
            )
        }

        image.use {
            cachedBitmap?.let { bitmap ->
                bitmap.copyPixelsFromBuffer(it.planes[0].buffer)
                if (livenessState.onFrameAvailable()) {
                    val outputLocations = arrayOf(
                        Array(FaceDetector.NUM_BOXES) {
                            FloatArray(FaceDetector.NUM_COORDS)
                        }
                    )
                    val outputScores = arrayOf(Array(FaceDetector.NUM_BOXES) { FloatArray(1) })
                    val outputMap = mapOf(0 to outputLocations, 1 to outputScores)
                    val tensorImage = tfImageBuffer.apply { load(cachedBitmap) }
                    val tfImage= getImageProcessor(it.imageInfo.rotationDegrees).process(tensorImage)

                    tfLite.runForMultipleInputsOutputs(arrayOf(tfImage.buffer), outputMap)

                    val facesFound = faceDetector.getBoundingBoxes(outputLocations, outputScores)
                    livenessState.onFrameFaceCountUpdate(facesFound.size)

                    if (facesFound.size > 1) return

                    facesFound.firstOrNull()?.let { detectedFace ->
                        val mirrorRectangle = FaceOval.convertMirroredRectangle(
                            detectedFace.location,
                            LivenessCoordinator.TARGET_WIDTH
                        )
                        val mirroredLeftEye = FaceOval.convertMirroredLandmark(
                            detectedFace.leftEye,
                            LivenessCoordinator.TARGET_WIDTH
                        )
                        val mirroredRightEye = FaceOval.convertMirroredLandmark(
                            detectedFace.rightEye,
                            LivenessCoordinator.TARGET_WIDTH
                        )
                        val mirroredMouth = FaceOval.convertMirroredLandmark(
                            detectedFace.mouth,
                            LivenessCoordinator.TARGET_WIDTH
                        )

                        livenessState.onFrameFaceUpdate(
                            mirrorRectangle,
                            mirroredLeftEye,
                            mirroredRightEye,
                            mirroredMouth
                        )
                    }
                }
            }
        }
    }

    private fun getImageProcessor(imageRotationDegrees: Int): ImageProcessor {
        val existingImageProcessor = tfImageProcessor
        if (existingImageProcessor != null) return existingImageProcessor

        val tfInputSize = tfLite.getInputTensor(0).shape().let {
            Size(it[2], it[1])
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(
                ResizeOp(
                    tfInputSize.height,
                    tfInputSize.width,
                    ResizeOp.ResizeMethod.NEAREST_NEIGHBOR
                )
            )
            .add(Rot90Op(-imageRotationDegrees / 90))
            .add(NormalizeOp(0f, 255f)) // transform RGB values from [-255, 255] to [-1, 1]
            .build()

        this.tfImageProcessor = imageProcessor
        return imageProcessor
    }
}
