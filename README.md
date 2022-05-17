# Exercise Companion Workshop

## Step 8: Validate the pose

1. When discussing about validation we can start by thinking about the errors we would emmit. We
   define a error for each leg and each angle we are interested.

```kotlin
enum class PoseError {
    LEFT_KNEE_NOT_90_DEGREES,
    RIGHT_KNEE_NOT_90_DEGREES,
    LEFT_HIP_NOT_90_DEGREES,
    RIGHT_HIP_NOT_90_DEGREES,
}
```

2. We would need a `ValidatedBodyPose` to express the result in a `BodyPoseState`.

```kotlin
sealed interface ValidatedPose : BodyPoseState {
    val pose: Pose
}

data class ValidBodyPose(
    override val pose: Pose,
) : ValidatedPose

data class InvalidBodyPose(
    override val pose: Pose,
    val error: PoseError,
) : ValidatedPose
```

3. Define a constraint for each `PoseError`.

```kotlin
val constraints = mapOf(
    PoseError.LEFT_KNEE_NOT_90_DEGREES to AngleConstraint(
        landmarks = AngleConstraint.Landmarks(
            a = PoseLandmark.LEFT_HIP,
            b = PoseLandmark.LEFT_KNEE,
            c = PoseLandmark.LEFT_ANKLE,
        ),
        minDegree = 75f,
        maxDegree = 115f,
    ),

    PoseError.RIGHT_KNEE_NOT_90_DEGREES to AngleConstraint(
        landmarks = AngleConstraint.Landmarks(
            a = PoseLandmark.RIGHT_HIP,
            b = PoseLandmark.RIGHT_KNEE,
            c = PoseLandmark.RIGHT_ANKLE,
        ),
        minDegree = 75f,
        maxDegree = 115f,
    ),

    PoseError.LEFT_HIP_NOT_90_DEGREES to AngleConstraint(
        landmarks = AngleConstraint.Landmarks(
            a = PoseLandmark.LEFT_SHOULDER,
            b = PoseLandmark.LEFT_HIP,
            c = PoseLandmark.LEFT_KNEE,
        ),
        minDegree = 75f,
        maxDegree = 115f,
    ),

    PoseError.RIGHT_HIP_NOT_90_DEGREES to AngleConstraint(
        landmarks = AngleConstraint.Landmarks(
            a = PoseLandmark.RIGHT_SHOULDER,
            b = PoseLandmark.RIGHT_HIP,
            c = PoseLandmark.RIGHT_KNEE,
        ),
        minDegree = 75f,
        maxDegree = 115f,
    ),
)
```

4. Validate the pose against each error we defined.

```kotlin
fun Pose.validate(): ValidatedPose = PoseError.values()
    .map { error ->
        val isValid = constraints[error]?.validate(this) ?: false
        error.takeIf { !isValid }
    }.firstOrNull { it != null }
    ?.let { error ->
        InvalidBodyPose(this, error)
    } ?: ValidBodyPose(this)
```

5. Validate the `Pose` in the `Analyzer`.
```kotlin
detector.process(mlImage)
   .addOnSuccessListener { pose ->
      callback.invoke(
         Result.success(pose.validate()),
      )
   }
```

## Next Step: Show the Body Pose

[Step 9: Show the Body Pose](../../tree/step_09)
