package ch.app.builders.validation

import android.graphics.PointF
import ch.app.builders.model.LandmarkType
import com.google.mlkit.vision.pose.Pose
import dev.romainguy.kotlin.math.*
import kotlin.math.abs
import kotlin.math.acos

interface Constraint {
    fun validate(pose: Pose): Boolean
}

internal class AngleConstraint(
    private val landmarks: Landmarks,
    private val minDegree: Float? = null,
    private val maxDegree: Float? = null,
) : Constraint {

    override fun validate(pose: Pose): Boolean = landmarks.angleInBodyPose(pose)
        ?.let { angle ->
            (minDegree?.let { min -> abs(angle) >= min } ?: true) &&
                    (maxDegree?.let { max -> abs(angle) <= max } ?: true)
        } ?: false

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
}

fun Pose.position(type: LandmarkType): Float2? =
    allPoseLandmarks.firstOrNull { it.landmarkType == type }?.position?.asVector

val PointF.asVector get() = Float2(x, y)

fun angleBetweenThreePoints(first: Float2, middle: Float2, last: Float2): Float =
    (first segmentWith middle) angleBetween (last segmentWith middle)

infix fun Float2.segmentWith(v: Float2) = Float2(x - v.x, y - v.y)

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
