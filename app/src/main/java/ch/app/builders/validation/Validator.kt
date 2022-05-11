package ch.app.builders.validation

import ch.app.builders.model.InvalidBodyPose
import ch.app.builders.model.PoseError
import ch.app.builders.model.SourceImage
import ch.app.builders.model.ValidBodyPose
import ch.app.builders.model.ValidatedPose
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

fun Pose.validate(sourceImage: SourceImage): ValidatedPose = PoseError.values()
    .map { error ->
        val isValid = constraints[error]?.validate(this) ?: false
        error.takeIf { !isValid }
    }.firstOrNull { it != null }
    ?.let { error ->
        InvalidBodyPose(this, sourceImage, error)
    } ?: ValidBodyPose(this, sourceImage)

private val constraints = mapOf(
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
