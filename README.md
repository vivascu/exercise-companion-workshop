# Exercise Companion Workshop

## Step 4: Add the Preview use case

1. Create a compose function `PreviewView` to wrap the CameraX `Preview` class and give it the 
   `PreviewView.ScaleType.FILL_CENTER` scale type. Use an `AndroidView` compose function to 
   create the view.

```kotlin
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

      previewView
   }
)
```

2. Create a `Preview` use case and give it the `previewView`'s `surfaceProvider` in the 
   `factory` block.  

```kotlin
    val previewUseCase = Preview.Builder()
        .build()
        .also { it.setSurfaceProvider(previewView.surfaceProvider) }
```

3. From our `Context` we need to obtain a `ProcessCameraProvider`. This process is asynchronous, 
   so we can wrap it in a `suspend` function.
```kotlin
suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, executor)
    }
}
```

For the executor we can just provide the main executor.

```kotlin
val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)
```

4. When we create the `PreviewView` we need to bind the use case to our `lifecycle`. For that we 
   will also need a `CameraSelector` to specify which camera to use. We can do it in the
   `factory` block.

```kotlin
coroutineScope.launch {
    val cameraProvider = context.getCameraProvider()
    try {
        // Must unbind the use-cases before rebinding them.
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            previewUseCase,
            )
        } catch (ex: Exception) {
            Log.e("CameraPreview", "Use case binding failed", ex)
        }
    }
```

5. Use the new function to display the camera preview.

```kotlin
@Composable
fun Exercise() {
    PermissionCheck {
        Box {
            CameraPreview()
        }
    }
}
```

6. Adjust insets for immersive experience.
Use the `Fullscreen` theme in `themes.xml`.
```xml
<style name="Theme.AppBuildersWorkshop" parent="android:Theme.Material.Light.NoActionBar.Fullscreen">
```

Add the dependency for the system UI controller accompanist library.

```
implementation "com.google.accompanist:accompanist-systemuicontroller:0.24.7-alpha"
```

In the activity specify that we will handle the insets. 

```kotlin
WindowCompat.setDecorFitsSystemWindows(window, false)
```

Use a side effect to adjust all the system bars colors.

```kotlin
val systemUiController = rememberSystemUiController()

SideEffect {
   systemUiController.setSystemBarsColor(
      color = Color.Transparent,
     )
   }
```

## Next Step: Add the Analysis use case

[Step 5: Add the Analysis use case](../../tree/step_05)
