package frc.robot

import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj.Timer
import frc.robot.interfaces.PoseProvider
import frc.robot.subsystems.AutoAlignCalc
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import kotlin.math.PI

internal class AutoAlignTest {
    private val mockPoseProvider: PoseProvider = mock()
    private val mockTimer: Timer = mock()

    @Test
    fun `getAlignSpeeds returns null when Limelight has no target`() {
        whenever(mockPoseProvider.tagPose).thenReturn(null)
        whenever(mockTimer.get()).thenReturn(2.0)

        AutoAlignCalc.poseProvider = mockPoseProvider

        val result = AutoAlignCalc.getAlignSpeeds(0.0, 0.0, mockTimer)
        assertNull(result)
    }

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

            whenever(mockPoseProvider.tagPose).thenReturn(pose)
            whenever(mockTimer.get()).thenReturn(2.0)

            AutoAlignCalc.poseProvider = mockPoseProvider

            val result = AutoAlignCalc.getAlignSpeeds(offsets[0], offsets[1], mockTimer)
            assertNotNull(result)
            assertEquals(expectedChassisSpeeds.vxMetersPerSecond, result!!.vxMetersPerSecond)
            assertEquals(expectedChassisSpeeds.vyMetersPerSecond, result.vyMetersPerSecond)
        }
    }
}