# Exercise Companion Workshop

## Step 9: Show the Body Pose

1. A way of thinking of the body is by representing it's skeleton which consists of `Bones`. 
   Define the bones visible in our guide.

```kotlin
data class Bone(
    val startJoint: LandmarkType,
    val endJoint: LandmarkType,
) {

    companion object {

        val LEFT_FOREARM = Bone(
            LEFT_WRIST,
            LEFT_ELBOW
        )

        val LEFT_UPPER_ARM = Bone(
            LEFT_ELBOW,
            LEFT_SHOULDER
        )

        val TORSO = Bone(
            LEFT_SHOULDER,
            RIGHT_SHOULDER
        )

        val RIGHT_FOREARM = Bone(
            RIGHT_WRIST,
            RIGHT_ELBOW
        )

        val RIGHT_UPPER_ARM = Bone(
            RIGHT_ELBOW,
            RIGHT_SHOULDER
        )

        val LEFT_SIDE_TORSO = Bone(
            LEFT_SHOULDER,
            LEFT_HIP
        )

        val RIGHT_SIDE_TORSO = Bone(
            RIGHT_SHOULDER,
            RIGHT_HIP
        )

        val WAIST = Bone(
            RIGHT_HIP,
            LEFT_HIP
        )

        val LEFT_THIGH = Bone(
            LEFT_HIP,
            LEFT_KNEE
        )

        val RIGHT_THIGH = Bone(
            RIGHT_HIP,
            RIGHT_KNEE
        )

        val LEFT_SHIN = Bone(
            LEFT_KNEE,
            LEFT_ANKLE
        )

        val RIGHT_SHIN = Bone(
            RIGHT_KNEE,
            RIGHT_ANKLE
        )

        /**
         * An order list representing the [Bone]s through which we have to draw a line to represent
         * a body skeleton.
         */
        val ALL = setOf(
            LEFT_FOREARM,
            LEFT_UPPER_ARM,
            TORSO,
            RIGHT_FOREARM,
            RIGHT_UPPER_ARM,
            LEFT_SIDE_TORSO,
            RIGHT_SIDE_TORSO,
            WAIST,
            LEFT_THIGH,
            RIGHT_THIGH,
            LEFT_SHIN,
            RIGHT_SHIN
        )

        fun Collection<Bone>.createSkeleton(): List<Int> =
            fold(listOf()) { acc, bone ->
                acc.toMutableList() + bone.startJoint + bone.endJoint
            }
    }
}
```

2. We just defined valid bones and we can use the `PoseError`s to specify which bones correspond 
   to them.
```kotlin
val PoseError?.bones: List<Bone>
    get() = when (this) {
        PoseError.LEFT_KNEE_NOT_90_DEGREES -> listOf(
            Bone.LEFT_THIGH,
            Bone.LEFT_SHIN,
        )

        PoseError.RIGHT_KNEE_NOT_90_DEGREES -> listOf(
            Bone.LEFT_THIGH,
            Bone.RIGHT_SHIN,
        )

        PoseError.LEFT_HIP_NOT_90_DEGREES -> listOf(
            Bone.LEFT_SIDE_TORSO,
            Bone.LEFT_THIGH,
        )
        PoseError.RIGHT_HIP_NOT_90_DEGREES -> listOf(
            Bone.RIGHT_SIDE_TORSO,
            Bone.RIGHT_THIGH
        )
        else -> emptyList()
    }
```

3. For the joints we will use a 3D point to represent them.

```kotlin
data class Point(
    val x: Float,
    val y: Float,
    val z: Float,
) {
    constructor(x: Number, y: Number, z: Number) : this(x.toFloat(), y.toFloat(), z.toFloat())
}
```

4. To draw the skeleton we will need some paint and information about the screen's density.

```kotlin
data class DrawingContext(
    val paint: Paint,
    val errorPaint: Paint,
    val screenDensity: Float,
)
```

5. Define the Skeleton as a group of valid and invalid joints and bones.

```kotlin
class Skeleton(
    val validBones: FloatArray,
    val validJoints: Set<Point>,
    val invalidBones: FloatArray = FloatArray(0),
    val invalidJoints: Set<Point> = emptySet(),
)
```

6. Add a draw function for the skeleton on a given `Canvas` given a `DrawingContext`.

```kotlin
    fun draw(canvas: Canvas, context: DrawingContext) {
        val jointRadius = DOT_RADIUS * context.screenDensity

        canvas.drawLines(validBones, context.paint)

        invalidBones.takeIf { it.isNotEmpty() }
            ?.let { bones ->
                canvas.drawLines(bones, context.errorPaint)
            }

        validJoints.forEach { point ->
            point.draw(canvas, jointRadius, context.paint)
        }

        invalidJoints.forEach { point ->
            point.draw(canvas, jointRadius, context.errorPaint)
        }

    }
```

Also we will draw a point as a circle to represent a joint on the skeleton.
```kotlin
fun Point.draw(canvas: Canvas, radius: Float, paint: Paint) {
        canvas.drawCircle(x, y, radius, paint)
}
```

6. Create the `Skeleton` out of a list of `Landmark`s.
```kotlin
private typealias BodyPose = Map<LandmarkType, Point>

internal fun BodyPose.createSkeleton(
    error: PoseError? = null,
): Skeleton {
    val errorBones = error.bones
    val bones = Bone.ALL.toMutableList() - errorBones
    return Skeleton(
        validBones = coordinatesForBones(bones),
        validJoints = jointsForBones(bones),
        invalidBones = coordinatesForBones(errorBones),
        invalidJoints = jointsForBones(errorBones),
    )
}


fun BodyPose.coordinatesForBones(bones: Collection<Bone>): FloatArray =
    bones.createSkeleton()
        .fold(listOf<Float>()) { acc, landmark ->
            acc.toMutableList() + getCoordinates(landmark)
        }.toFloatArray()

fun BodyPose.getCoordinates(type: LandmarkType): List<Float> = get(type)
    ?.let { landmark ->
        listOf(
            landmark.x,
            landmark.y
        )
    } ?: emptyList()

fun BodyPose.jointsForBones(bones: Collection<Bone>): Set<Point> =
    bones.createSkeleton()
        .fold(setOf()) { acc, landmark ->
            get(landmark)?.let { point ->
                acc.toMutableSet() + point
            } ?: acc
        }
```

8. The Skeleton will be drawn on a custom Android view that we will use as an overlay. Create 
   the custom android view that will draw a `Skeleton` for a `BodyPoseState`.

```kotlin
class GraphicOverlay @JvmOverloads constructor(
   context: Context,
   attrs: AttributeSet? = null,
   defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

   private val lock: Any = Any()

   private val screenDensity = resources.displayMetrics.density

   private val normalPaint by lazy {
      Paint().apply {
         strokeWidth = STROKE_WIDTH * screenDensity
         color = ContextCompat.getColor(context, R.color.green)
      }
   }

   private val errorPaint by lazy {
      Paint().apply {
         strokeWidth = STROKE_WIDTH * screenDensity
         color = ContextCompat.getColor(context, R.color.red)
      }
   }


   private val drawingContext =
      DrawingContext(normalPaint, errorPaint, screenDensity)

   private var skeleton: Skeleton? = null

   fun setBodyState(state: BodyPoseState) {
      if (state is ValidatedPose) {
         val bodyPose = state.pose
         val landmarks = getLandmarkPoints(bodyPose)
         with(landmarks) {
            synchronized(lock) {
               skeleton = when (state) {
                  is ValidBodyPose -> createSkeleton()
                  is InvalidBodyPose -> createSkeleton(state.error)
               }
            }
         }
      }
      postInvalidate()
   }

   private fun getLandmarkPoints(bodyPose: Pose) =
      bodyPose.allPoseLandmarks
         .associateBy { it.landmarkType }
         .mapValues { (_, landmark) ->
            with(landmark.position3D) {
               Point(
                  x = x,
                  y = y,
                  z = z,
               )
            }
         }

   override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
      synchronized(lock) {
         skeleton?.draw(canvas, drawingContext)
      }
   }

   companion object {
      private const val STROKE_WIDTH = 5f
   }
}
```

9. Use the `GraphicOverlay` in our composable function.
```kotlin
    AndroidView(
        modifier = modifier,
        factory = { context ->
            GraphicOverlay(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = {
            it.setBodyState(state.value)
        }
    )
```


## Next Step: Translate coordinates

[Step 10: Translate coordinates](../../tree/step_10)
