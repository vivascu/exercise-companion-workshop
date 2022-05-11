# Exercise Companion Workshop

## Step 10: Translate the coordinates

1. In order to be able to properly translate the coordinates we need to pass the information about
   the `SourceImage`.

```kotlin
data class SourceImage(
    val height: Int,
    val width: Int,
    val rotation: Int,
    val isFlipped: Boolean,
) {
    val rotatedHeight
        get() = if (rotation == 0 || rotation == 180) {
            height
        } else width

    val rotatedWidth
        get() = if (rotation == 0 || rotation == 180) {
            width
        } else height
}
```

2. As well we need to represent the `ViewPort` that will be the destination for the skeletal
   overlay.

```kotlin
data class ViewPort(val height: Int, val width: Int)

val ViewPort.aspectRatio: Float get() = width.toFloat() / height.toFloat()
```

3. Define a `CameraMode` to be able to keep determine if the source image is flipped.

```kotlin
enum class CameraMode {
    Front, Side
}
```

4. Extract the `SourceImage` info out of the ML Image.

```kotlin
private fun MlImage.asSource(cameraMode: CameraMode): SourceImage = SourceImage(
    width = width,
    height = height,
    rotation = rotation,
    isFlipped = cameraMode == CameraMode.Front
)
```

5. Pass the `SourceImage` from the image `Analazyer` to the `ValidatedState`s.

```kotlin
Result.success(
    pose.validate(mlImage.asSource(cameraMode)),
)
```

```kotlin
sealed interface ValidatedPose : BodyPoseState {
    val pose: Pose
    val sourceImage: SourceImage
}

data class ValidBodyPose(
    override val pose: Pose,
    override val sourceImage: SourceImage,
) : ValidatedPose

data class InvalidBodyPose(
    override val pose: Pose,
    override val sourceImage: SourceImage,
    val error: PoseError,
) : ValidatedPose
```

6. Translate the coordinates for each `Landmark`.

```kotlin
fun Pose.translateCoordinates(sourceImage: SourceImage, viewPort: ViewPort): Map<Int, Point> =
    allPoseLandmarks.associate { landmark ->
        landmark.translate(sourceImage, viewPort)
    }

fun PoseLandmark.translate(sourceImage: SourceImage, viewPort: ViewPort): Pair<Int, Point> {

    val viewAspectRatio = viewPort.aspectRatio
    val imageAspectRatio = sourceImage.rotatedWidth.toFloat() / sourceImage.rotatedHeight.toFloat()

    var postScaleWidthOffset = 0f
    var postScaleHeightOffset = 0f
    val scaleFactor: Float

    val viewWidth = viewPort.width.toFloat()
    val viewHeight = viewPort.height.toFloat()
    if (viewAspectRatio > imageAspectRatio) {
        scaleFactor = viewWidth / sourceImage.rotatedWidth.toFloat()
        postScaleHeightOffset =
            (viewWidth / imageAspectRatio - viewHeight) / 2
    } else {
        scaleFactor = viewHeight / sourceImage.rotatedHeight.toFloat()
        postScaleWidthOffset = (viewHeight * imageAspectRatio - viewWidth) / 2
    }

    val x = if (sourceImage.isFlipped) {
        viewWidth - (position3D.x * scaleFactor - postScaleWidthOffset)
    } else position3D.x * scaleFactor - postScaleWidthOffset

    val y = position3D.y * scaleFactor - postScaleHeightOffset

    return landmarkType to Point(x = x, y = y, z = position3D.z)
}
```

7. Use the translated landmarks to draw the `Skeleton`.

```kotlin
fun setBodyState(state: BodyPoseState) {
    val landmarks = bodyPose.translateCoordinates(state.sourceImage, ViewPort(height, width))
}
```

## Next Step: Count the exercise

[Step 11: Count the exercise](../../tree/step_11)
