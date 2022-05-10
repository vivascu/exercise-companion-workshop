package ch.app.builders.detection

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import ch.app.builders.model.BodyPoseState

class ImageProcessor(
    private val callback: (Result<BodyPoseState>) -> Unit,
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        //TODO Add analysis
    }
}
