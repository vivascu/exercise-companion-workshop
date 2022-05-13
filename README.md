# Exercise Companion Workshop

## Step 11: Count the exercise

1. In order to count the exercise which in our case is time bound we can start an effect to count
   the time.

```kotlin
@Composable
private fun Countdown(
    onComplete: () -> Unit,
) {
    LaunchedEffect(key1 = "countdown") {
        delay(EXERCISE_DURATION)
        onComplete()
    }
}
```

2. We can use this function when the resulting state is valid in the preview function.

```kotlin

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA,
    exerciseComplete: () -> Unit,
) {

    ...
    if (state.value is ValidBodyPose) {
        Countdown {
            exerciseComplete()
        }
    }
    ...
```

3. Once the result is counted we can navigate away in to the result screen. We will keep count of
   the completion state in the and draw the appropriate screen.

```kotlin
@Composable
fun Exercise() {

...
    val isComplete = remember { mutableStateOf(false) }

    if (isComplete.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "You are done for today!")

            Button(onClick = { isComplete.value = false }) {
                Text(text = "Repeat")
            }
        }
    } else {
        PermissionCheck {
            Box {
                CameraPreview {
                    isComplete.value = true
                }
            }
        }
    }
}
```

## Next steps

Train a model to count repetitions: 
- [ML KIT example of repetition counting](https://developers.google.com/ml-kit/vision/pose-detection/classifying-poses#3_train_the_model_and_count_repetitions)
