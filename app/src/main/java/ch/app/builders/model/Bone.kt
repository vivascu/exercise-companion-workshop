package ch.app.builders.model

import com.google.mlkit.vision.pose.PoseLandmark.LEFT_ANKLE
import com.google.mlkit.vision.pose.PoseLandmark.LEFT_ELBOW
import com.google.mlkit.vision.pose.PoseLandmark.LEFT_HIP
import com.google.mlkit.vision.pose.PoseLandmark.LEFT_KNEE
import com.google.mlkit.vision.pose.PoseLandmark.LEFT_SHOULDER
import com.google.mlkit.vision.pose.PoseLandmark.LEFT_WRIST
import com.google.mlkit.vision.pose.PoseLandmark.RIGHT_ANKLE
import com.google.mlkit.vision.pose.PoseLandmark.RIGHT_ELBOW
import com.google.mlkit.vision.pose.PoseLandmark.RIGHT_HIP
import com.google.mlkit.vision.pose.PoseLandmark.RIGHT_KNEE
import com.google.mlkit.vision.pose.PoseLandmark.RIGHT_SHOULDER
import com.google.mlkit.vision.pose.PoseLandmark.RIGHT_WRIST

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
