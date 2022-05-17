package ch.app.builders.model

import com.google.mlkit.vision.pose.Pose

sealed interface BodyPoseState

object Idle : BodyPoseState

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