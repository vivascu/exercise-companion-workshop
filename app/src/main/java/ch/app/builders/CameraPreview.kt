package ch.app.builders

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import ch.app.builders.detection.ImageProcessor
import ch.app.builders.model.BodyPoseState
import ch.app.builders.model.Idle
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
) {
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // CameraX Analysis UseCase
    val analysisUseCase = ImageAnalysis.Builder()
        .build()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            // CameraX Preview UseCase
            val previewUseCase = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }


            coroutineScope.launch {
                val cameraProvider = context.getCameraProvider()
                try {
                    // Must unbind the use-cases before rebinding them.
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        previewUseCase,
                        analysisUseCase,
                    )
                } catch (ex: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", ex)
                }
            }


            previewView
        }
    )

    val state = analysisUseCase.detectPose(LocalContext.current.executor)
        .collectAsState(initial = Idle)
}

private fun ImageAnalysis.detectPose(
    executor: Executor,
): Flow<BodyPoseState> = callbackFlow {
    analyze(executor) { result ->
        with(result) {
            onSuccess {
                trySend(it)
            }
            onFailure { cancel("Image Process Failure", it) }
        }
    }

    awaitClose {}
}

@SuppressLint("UnsafeOptInUsageError")
private fun ImageAnalysis.analyze(
    executor: Executor,
    callback: (Result<BodyPoseState>) -> Unit,
) {
    val imageProcessor = ImageProcessor(callback)
    setAnalyzer(executor) { imageProxy ->
        imageProcessor.analyze(imageProxy)
    }
}
