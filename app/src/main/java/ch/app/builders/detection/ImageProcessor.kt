package ch.app.builders.detection

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import ch.app.builders.model.BodyPoseState
import ch.app.builders.model.Idle
import ch.app.builders.validation.validate
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

class ImageProcessor(
    private val callback: (Result<BodyPoseState>) -> Unit,
) : ImageAnalysis.Analyzer {

    private val detector by lazy {
        PoseDetection.getClient(
            PoseDetectorOptions.Builder()
                .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                .build()
        )
    }

    @ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        val mlImage = image.mlImage
        detector.process(mlImage)
            .addOnSuccessListener { pose ->
                callback.invoke(
                    Result.success(pose.validate()),
                )
            }
            .addOnFailureListener { exception ->
                callback.invoke(Result.failure(exception))
            }
            .addOnCompleteListener { image.close() }
    }
}
