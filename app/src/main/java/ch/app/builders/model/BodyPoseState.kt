package ch.app.builders.model

import com.google.mlkit.vision.pose.Pose

sealed interface BodyPoseState

object Idle : BodyPoseState

sealed interface ValidatedPose : BodyPoseState {
    val pose: Pose
    val sourceImage: SourceImage
}

data class ValidBodyPose(
    override val pose: Pose,
    override val sourceImage: SourceImage,
) : ValidatedPose

data class InvalidBodyPose(
    override val pose: Pose,
    override val sourceImage: SourceImage,
    val error: PoseError,
) : ValidatedPose
