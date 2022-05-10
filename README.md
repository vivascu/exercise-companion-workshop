# Exercise Companion Workshop

## Step 2: Request the necessary permissions

Before the app opens the camera, it needs permission from the user to do so; In this step, we'll
implement those necessary permissions.

1. Open `AndroidManifest.xml` and add these lines before the `application` tag.
```xml
    <uses-feature android:name="android.hardware.camera" />
    <uses-permission android:name="android.permission.CAMERA" />
```
2. We are using compose therefore can use the [Accompanist Permission Library](https://google.github.io/accompanist/permissions/). Add the dependency to `build.gradle`

```
implementation 'com.google.accompanist:accompanist-permissions:0.24.7-alpha'
```


3. Create a composable function `PermissionCheck` that will handle the permission check.

```kotlin
@Composable
fun PermissionCheck(
    content: @Composable () -> Unit = { }
) {
// Camera permission state
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    when (val status = cameraPermissionState.status) {
        // If the camera permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            content()
        }
        is PermissionStatus.Denied -> {
            Column {
                val textToShow = if (status.shouldShowRationale) {
                    // If the user has denied the permission but the rationale can be shown,
                    // then gently explain why the app requires this permission
                    "The camera is important for this app. Please grant the permission."
                } else {
                    // If it's the first time the user lands on this feature, or the user
                    // doesn't want to be asked again for this permission, explain that the
                    // permission is required
                    "Camera permission required for this feature to be available. " +
                            "Please grant the permission."
                }
                Text(textToShow)
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Request permission")
                }
            }
        }
    }
}
```

4. Use the new function in the `MainActivity`.

```kotlin
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Exercise() {
    PermissionCheck {
        Box {
            Text(text = "Hello AppBuilders!")
        }
    }
}
```

## Next Step: Add CameraX

[Step 3: Add CameraX](../../tree/step_03)
