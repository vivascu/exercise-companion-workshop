package ch.app.builders.model

/**
 * The error describing an invalid body pose.
 */
enum class PoseError {
    /**
     * The legs are not 90 degrees.
     */
    LEFT_KNEE_NOT_90_DEGREES,

    /**
     * The legs are not 90 degrees.
     */
    RIGHT_KNEE_NOT_90_DEGREES,

    /**
     * The hips are not 90 degrees.
     */
    LEFT_HIP_NOT_90_DEGREES,

    /**
     * The hips are not 90 degrees.
     */
    RIGHT_HIP_NOT_90_DEGREES,

}

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
