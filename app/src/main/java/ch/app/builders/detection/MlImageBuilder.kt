package ch.app.builders.detection

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.odml.image.MediaMlImageBuilder

internal val ImageProxy.mlImage
    @ExperimentalGetImage
    get() = MediaMlImageBuilder(image!!)
        .setRotation(imageInfo.rotationDegrees)
        .build()
