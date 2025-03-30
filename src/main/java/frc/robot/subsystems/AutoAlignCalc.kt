package frc.robot.subsystems

import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj.Timer
import frc.robot.Constants
import frc.robot.interfaces.PoseProvider
import kotlin.math.*

object AutoAlignCalc {
    var poseProvider: PoseProvider = LimelightSubsystem
    fun getAlignSpeeds(
        goalX: Double,
        goalZ: Double,
        timer: Timer
    ) : ChassisSpeeds? {
        val limelightResults: Pose3d = poseProvider.getTagPosition() ?: return null

        timer.reset()

        // Units are in meters and radians
        var x: Double = limelightResults.x
        var z: Double = limelightResults.z
        val yaw: Double = limelightResults.rotation.z

        // Calculate the offsets for the Apriltag
        x -= (cos(yaw) * goalZ) - (sin(yaw) * goalX)
        z += (sin(yaw) * goalZ) + (cos(yaw) * goalX)

        // Normalize the x and z values
        if (abs(x) > abs(z)) {
            z = abs(z) / abs(x)
            x = 1.0
        } else {
            z = 1.0
            x = abs(x) / abs(z)
        }

        if (z < 0) z *= -1.0

        if (x < 0) x *= -1.0

        x = if (x < Constants.AlignConstants.ALIGN_DEADZONE) 0.0 else x
        z = if (z < Constants.AlignConstants.ALIGN_DEADZONE) 0.0 else z

        var dist = sqrt(x.pow(2) + z.pow(2))

        dist = if (dist > Constants.AlignConstants.FINE_ALIGN_DEADZONE) 1.0 else dist / Constants.AlignConstants.FINE_ALIGN_DEADZONE

        // Calculate the rotation speed
        val rotSign: Double = if (abs(yaw) < Constants.AlignConstants.ALIGN_ROT_DEADZONE) 0.0 else yaw/abs(yaw)

        var angularDistance = if (abs(yaw) > Constants.AlignConstants.FINE_ALIGN_ROT_DEADZONE) 1.0 else max(0.2, abs(yaw)/Constants.AlignConstants.FINE_ALIGN_ROT_DEADZONE)

        // Finalize drivetrain controls
        dist = sqrt(dist)
        angularDistance = sqrt(angularDistance)

        return ChassisSpeeds(
            z * dist * Constants.AlignConstants.MAX_SPEED,
            -x * dist * Constants.AlignConstants.MAX_SPEED,
            rotSign * angularDistance * Constants.AlignConstants.MAX_ANGULAR_SPEED,
        )

    }
}