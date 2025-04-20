package frc.robot.subsystems

import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import frc.robot.Constants
import kotlin.math.*

object AutoAlignCalc {
    fun getAlignSpeeds(
        goalX: Double,
        goalZ: Double,
        curPose: Pose3d
    ) : ChassisSpeeds {
        // Units are in meters and radians
        var x: Double = -curPose.x // Inverted to match the robot's coordinate system
        var z: Double = curPose.z
        val yaw: Double = curPose.rotation.z

        var x_normalized = 1.0
        var z_normalized = 1.0

        // Calculate the offsets for the Apriltag
        x -= (sin(yaw) * goalZ) + (cos(yaw) * goalX)
        z += -(cos(yaw) * goalZ) - (sin(yaw) * goalX)

        // Calculate the distance to the target
        var dist = sqrt(x.pow(2) + z.pow(2))
        dist = if (dist > Constants.AlignConstants.FINE_ALIGN_DEADZONE) 1.0 else dist / Constants.AlignConstants.FINE_ALIGN_DEADZONE

        // Normalize the x and z values
        if (abs(x) > abs(z)) {
            z_normalized = abs(z) / abs(x)
            x_normalized = 1.0
        }
        if (abs(x) < abs(z)) {
            z_normalized = 1.0
            x_normalized = abs(x) / abs(z)
        }

        if (z < 0) z_normalized *= -1.0

        if (x > 0) x_normalized *= -1.0

        x_normalized = if (abs(x) < Constants.AlignConstants.ALIGN_DEADZONE) 0.0 else x_normalized
        z_normalized = if (abs(z) < Constants.AlignConstants.ALIGN_DEADZONE) 0.0 else z_normalized


        // Calculate the rotation speed
        val rotSign: Double = if (abs(yaw) < Constants.AlignConstants.ALIGN_ROT_DEADZONE) 0.0 else yaw/abs(yaw)

        var angularDistance = if (abs(yaw) > Constants.AlignConstants.FINE_ALIGN_ROT_DEADZONE) 1.0 else max(0.2, abs(yaw)/Constants.AlignConstants.FINE_ALIGN_ROT_DEADZONE)

        // Finalize drivetrain controls
        dist = sqrt(dist)
        angularDistance = sqrt(angularDistance)

        return ChassisSpeeds(
            z_normalized * dist * Constants.AlignConstants.MAX_SPEED,
            -x_normalized * dist * Constants.AlignConstants.MAX_SPEED,
            -rotSign * angularDistance * Constants.AlignConstants.MAX_ANGULAR_SPEED,
        )

    }
}