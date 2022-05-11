# Exercise Companion Workshop

## Step 7: Add constraints

We can identify a valid pose by computing the angles of various joints.

Our simple pose can be described as the following combination of approximate body part angles:

- 90 degree angle at both knees
- 90 degree angle at the front of the legs and waist

We use the pose landmarks to compute these angles.

1. Add the `Constraint` interface that can validate a `Pose`.

```kotlin
interface Constraint {
    fun validate(pose: Pose): Boolean
}
```

2. Create a `Constraint` that will validate an angle. For that we will have to specify three point
   in the `Pose` with the use of the `LandmarkType`.

Since the `LandmarkType` is an `Int` in the ML Kit we can define a typealias for readability.

```kotlin
typealias LandmarkType = Int
```

```kotlin
data class Landmarks(
    val a: LandmarkType,
    val b: LandmarkType,
    val c: LandmarkType,
) 
```

And the `AngleConstraint` should specify the threshold for the valid angle.

```kotlin
internal class AngleConstraint(
    private val landmarks: Landmarks,
    private val minDegree: Float? = null,
    private val maxDegree: Float? = null,
) : Constraint {

    override fun validate(pose: Pose): Boolean {
        ...
    }   
```

3. In order to compute the angles we will use some math and for that we need the `kotlin-math`
   dependency.

```
implementation 'dev.romainguy:kotlin-math:1.3.0'
```

4. Determine the vector out of the position of a specified `LandmarkType` for a given `Pose`.

```kotlin
fun Pose.position(type: LandmarkType): Float2? =
    allPoseLandmarks.firstOrNull { it.landmarkType == type }?.position?.asVector

val PointF.asVector get() = Float2(x, y)
```

5. Create a function to determine the angle between two vectors.

```kotlin
infix fun Float2.angleBetween(v: Float2) =
    degrees(
        acos(
            clamp(
                x = dot(this, v) / (length(this) * length(v)),
                min = -1f,
                max = 1f,
            )
        )
    )
```

6. Create a function to define a segment out of two vectors.

```kotlin
infix fun Float2.segmentWith(v: Float2) = Float2(x - v.x, y - v.y)
```

7. Create a function that combines the previous ones to compute the angle between three points.

```kotlin
fun angleBetweenThreePoints(first: Float2, middle: Float2, last: Float2): Float =
    (first segmentWith middle) angleBetween (last segmentWith middle)
```

8. Use the `angleBetweenThreePoints` and landmarks position to find their angle in a `Pose`.

```kotlin
    data class Landmarks(
        val a: LandmarkType,
        val b: LandmarkType,
        val c: LandmarkType,
    ) {
        fun angleInBodyPose(pose: Pose): Float? {
            val first = pose.position(a)
            val middle = pose.position(b)
            val last = pose.position(c)

            if (first == null || middle == null || last == null) return null

            return angleBetweenThreePoints(
                first,
                middle,
                last,
            )
        }
    }
```

9. Validate the computed angle in the `AngleConstraint`.

```kotlin
    override fun validate(pose: Pose): Boolean = landmarks.angleInBodyPose(pose)
        ?.let { angle ->
            (minDegree?.let { min -> abs(angle) >= min } ?: true) &&
                    (maxDegree?.let { max -> abs(angle) <= max } ?: true)
        } ?: false
```

## Next Step: Validate the pose

[Step 8: Validate the pose](../../tree/step_08)
