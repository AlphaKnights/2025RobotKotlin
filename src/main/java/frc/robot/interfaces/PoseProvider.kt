package frc.robot.interfaces

import edu.wpi.first.math.geometry.Pose3d

interface PoseProvider {
    fun getTagPosition(): Pose3d?
}