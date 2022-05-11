package ch.app.builders.ui

import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.VisibleForTesting
import ch.app.builders.model.Bone
import ch.app.builders.model.Bone.Companion.createSkeleton
import ch.app.builders.model.LandmarkType
import ch.app.builders.model.Point
import ch.app.builders.model.PoseError
import ch.app.builders.model.bones

internal  class Skeleton(
    val validBones: FloatArray,
    val validJoints: Set<Point>,
    val invalidBones: FloatArray = FloatArray(0),
    val invalidJoints: Set<Point> = emptySet(),
) {
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

    private fun Point.draw(canvas: Canvas, radius: Float, paint: Paint) {
        canvas.drawCircle(x, y, radius, paint)
    }

    companion object {
        internal const val DOT_RADIUS = 8f
    }
}

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
