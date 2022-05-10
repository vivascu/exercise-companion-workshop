# Exercise Companion Workshop

## Step 3: Add CameraX

1. Open the `build.gradle` file and add the CameraX dependencies:
```
    def camerax_version = "1.1.0-beta03"
    implementation "androidx.camera:camera-core:${camerax_version}"
    implementation "androidx.camera:camera-camera2:${camerax_version}"
    implementation "androidx.camera:camera-lifecycle:${camerax_version}"

    implementation "androidx.camera:camera-view:${camerax_version}"
    implementation "androidx.camera:camera-extensions:${camerax_version}"
```

2. CameraX needs some methods that are part of Java 8, so we need to set our compile options accordingly. At the end of the android block, right after buildTypes, add the following:

```
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

## Next Step: Add the Preview use case

[Step 4: Add the Preview use case](../../tree/step_04)
