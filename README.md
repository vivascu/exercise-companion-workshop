# Exercise Companion Workshop

## Step 5: Add the Analysis use case

A great way to make our camera app more interesting is using the `ImageAnalysis` feature. It allows
us to define a custom class that implements the `ImageAnalysis.Analyzer` interface, and which will
be called with incoming camera frames.

1. Create a class that implements the `ImageAnalysis.Analyzer` interface.

2. Create the Analysis use case in the `CameraPreview` function.

```kotlin
// CameraX Analysis UseCase
val analysisUseCase = ImageAnalysis.Builder().build()
```

3. Bind the use case to the lifecycle of the composable method in the `factory` block.

```kotlin
cameraProvider.bindToLifecycle(
    lifecycleOwner,
    cameraSelector,
    previewUseCase,
    analysisUseCase,
)
```

4. In order to follow the results of the analysis we want to connect the `Analyser` from the first 
   step to the use case. The binding requires an `Executor` on which to run the analysis. 

```kotlin
fun ImageAnalysis.analyze(
    executor: Executor,
) {
    val imageProcessor = ImageProcessor()
    setAnalyzer(executor) { imageProxy ->
        imageProcessor.analyze(imageProxy)
    }
}
```

5. We can make it observe the updates by wrapping the result of the analysis in a `Flow`. The 
   emitted states we will define as `BodyPoseStates` with a default `Idle` value. These states 
   will come from our `Analyzer` via a callback.

```kotlin
class ImageProcessor(
    private val callback: (Result<BodyPoseState>) -> Unit,
) : ImageAnalysis.Analyzer
```

Pass the callback further up when creating the image processor.

```kotlin
private fun ImageAnalysis.analyze(
    executor: Executor,
    callback: (Result<BodyPoseState>) -> Unit,
) {
    val imageProcessor = ImageProcessor(callback)
    setAnalyzer(executor) { imageProxy ->
        imageProcessor.analyze(imageProxy)
    }
}
```

6. Create a function that will provide the actual `Flow` and handle the `Result<BodyPoseState>`.

```kotlin
private fun ImageAnalysis.detectPose(
    executor: Executor,
): Flow<BodyPoseState> = callbackFlow {
    analyze(executor) { result ->
        with(result) {
            onSuccess { trySend(it) }
            onFailure { cancel("Image Process Failure", it) }
        }
    }

    awaitClose {}
}
```

## Next Step: Add the pose detection

[Step 6: Add the pose detection](../../tree/step_06)
