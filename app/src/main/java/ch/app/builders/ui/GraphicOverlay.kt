package ch.app.builders.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import ch.app.builders.R
import ch.app.builders.model.BodyPoseState
import ch.app.builders.model.InvalidBodyPose
import ch.app.builders.model.Point
import ch.app.builders.model.ValidBodyPose
import ch.app.builders.model.ValidatedPose
import com.google.mlkit.vision.pose.Pose

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
