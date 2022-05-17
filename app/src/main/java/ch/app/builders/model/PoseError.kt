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