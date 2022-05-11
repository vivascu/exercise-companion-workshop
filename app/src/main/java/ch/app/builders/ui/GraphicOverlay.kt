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
import ch.app.builders.model.SourceImage
import ch.app.builders.model.ValidBodyPose
import ch.app.builders.model.ValidatedPose
import ch.app.builders.model.ViewPort
import ch.app.builders.model.aspectRatio
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

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
            val landmarks = bodyPose.translateCoordinates(state.sourceImage, ViewPort(height, width))
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

fun Pose.translateCoordinates(sourceImage: SourceImage, viewPort: ViewPort): Map<Int, Point> =
    allPoseLandmarks.associate { landmark ->
        landmark.translate(sourceImage, viewPort)
    }


fun PoseLandmark.translate(sourceImage: SourceImage, viewPort: ViewPort): Pair<Int, Point> {

    val viewAspectRatio = viewPort.aspectRatio
    val imageAspectRatio = sourceImage.rotatedWidth.toFloat() / sourceImage.rotatedHeight.toFloat()

    var postScaleWidthOffset = 0f
    var postScaleHeightOffset = 0f
    val scaleFactor: Float

    val viewWidth = viewPort.width.toFloat()
    val viewHeight = viewPort.height.toFloat()
    if (viewAspectRatio > imageAspectRatio) {
        scaleFactor = viewWidth / sourceImage.rotatedWidth.toFloat()
        postScaleHeightOffset =
            (viewWidth / imageAspectRatio - viewHeight) / 2
    } else {
        scaleFactor = viewHeight / sourceImage.rotatedHeight.toFloat()
        postScaleWidthOffset = (viewHeight * imageAspectRatio - viewWidth) / 2
    }

    val x = if (sourceImage.isFlipped) {
        viewWidth - (position3D.x * scaleFactor - postScaleWidthOffset)
    } else position3D.x * scaleFactor - postScaleWidthOffset

    val y = position3D.y * scaleFactor - postScaleHeightOffset

    return landmarkType to Point(x = x, y = y, z = position3D.z)
}
