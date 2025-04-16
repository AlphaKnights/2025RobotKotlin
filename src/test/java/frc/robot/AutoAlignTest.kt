package frc.robot

import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import frc.robot.subsystems.AutoAlignCalc
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.PI

internal class AutoAlignTest {
    @Test
    fun `getAlignSpeeds returns correct ChassisSpeeds when Limelight has target with various positions`() {
        // Various mock positions and expected results
        val mockPoses: Array<Array<Any>> = arrayOf(
            arrayOf(
                Pose3d(
                    Translation3d(
                        0.0, 0.0, 1.0
                    ),
                    Rotation3d(
                        0.0, 0.0, 0.0
                    ),
                ),
                doubleArrayOf(
                    0.0, 0.0
                ),
                ChassisSpeeds(
                    1.0, -0.0, 0.0
                ),

                Pose3d(
                    Translation3d(
                        0.4, 0.0, 0.3
                    ),
                    Rotation3d(
                        0.0, 0.0, PI / 6
                    ),
                ),
                doubleArrayOf(
                    0.3, 0.4
                ),
                ChassisSpeeds(
                    -0.6469387689401926, 1.0, 0.0
                ),

                Pose3d(
                    Translation3d(
                        0.5, 0.0, 2.0
                    ),
                    Rotation3d(
                        0.0, 0.0, -PI / 6
                    ),
                ),
                doubleArrayOf(
                    -0.3, 0.2
                ),
                ChassisSpeeds(
                    1.0, -0.34884414872803654, 0.0
                ),

            )
        )
        for (test in mockPoses) {
            val pose = test[0] as Pose3d
            val offsets = test[1] as DoubleArray
            val expectedChassisSpeeds = test[2] as ChassisSpeeds

            val result = AutoAlignCalc.getAlignSpeeds(offsets[0], offsets[1], pose)
            assertEquals(expectedChassisSpeeds.vxMetersPerSecond, result.vxMetersPerSecond)
            assertEquals(expectedChassisSpeeds.vyMetersPerSecond, result.vyMetersPerSecond)
        }
    }

    @Test
    fun `getAlignSpeeds returns zero speeds when within deadzone`() {
        val pose = Pose3d(
            Translation3d(
                0.0, 0.0, 0.0
            ),
            Rotation3d(
                0.0, 0.0, 0.0
            ),
        )
        val expectedChassisSpeeds = ChassisSpeeds(0.0, -0.0, 0.0)

        val result = AutoAlignCalc.getAlignSpeeds(0.0, 0.0, pose)
        assertEquals(expectedChassisSpeeds.vxMetersPerSecond, result.vxMetersPerSecond)
        assertEquals(expectedChassisSpeeds.vyMetersPerSecond, result.vyMetersPerSecond)
    }

    @Test
    fun `getAlignSpeeds returns correct speeds when directly behind tag`() {
        val pose = Pose3d(
            Translation3d(
                0.0, 0.0, 1.0
            ),
            Rotation3d(
                0.0, 0.0, 0.0
            ),
        )
        val offsets = doubleArrayOf(0.0, 0.0)
        val expectedChassisSpeeds = ChassisSpeeds(1.0, -0.0, 0.0)

        val result = AutoAlignCalc.getAlignSpeeds(offsets[0], offsets[1], pose)
        assertEquals(expectedChassisSpeeds.vxMetersPerSecond, result.vxMetersPerSecond)
        assertEquals(expectedChassisSpeeds.vyMetersPerSecond, result.vyMetersPerSecond)
        assertEquals(expectedChassisSpeeds.omegaRadiansPerSecond, result.omegaRadiansPerSecond)
    }
}