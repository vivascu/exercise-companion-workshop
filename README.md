# Exercise Companion Workshop

## Step 6: Add pose detection

For pose detection we a going to use the [Pose detection library from ML Kit](https://developers.google.com/ml-kit/vision/pose-detection/android).

ML Kit Pose Detection produces a full-body 33 point skeletal match that includes facial landmarks (
ears, eyes, mouth, and nose) and points on the hands and feet. We will use these landmarks to
determine if the exercises are performed correctly.

1. Add the dependency for ML Kit.

```
implementation 'com.google.mlkit:pose-detection:18.0.0-beta2'
```

2. Get a pose detection client in the image analyzer.

```
private val detector by lazy {
    PoseDetection.getClient(
        PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        )
    }
```

3. Convert the `ImageProxy` to an `MLImage`.

```
val ImageProxy.mlImage
    @ExperimentalGetImage
    get() = MediaMlImageBuilder(image!!)
        .setRotation(imageInfo.rotationDegrees)
        .build()
```

4. Process the ML task in the `analyze` method.

```
@ExperimentalGetImage
override fun analyze(image: ImageProxy) {
    val mlImage = image.mlImage
    detector.process(mlImage)
        .addOnSuccessListener { pose ->
            callback.invoke(Result.success(Idle),) // Return while we don't validate the pose.
        }
        .addOnFailureListener { exception ->
            callback.invoke(Result.failure(exception))
        }
        .addOnCompleteListener { image.close() }
    }
```

5. Collect the flow in the camera view so we bring in the pose detection.

```
analysisUseCase.detectPose(LocalContext.current.executor).collectAsState(initial = Idle)
```

## Next Step: Add constraints

[Step 7: Add constraints](../../tree/step_07)
