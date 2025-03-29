package frc.robot


import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.wpilibj.Timer
import frc.robot.interfaces.PoseProvider
import frc.robot.subsystems.AutoAlignCalc
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

internal class AutoAlignTest {
    private val mockPoseProvider: PoseProvider = mock()
    private val mockTimer: Timer = mock()

    @Test
    fun `getAlignSpeeds returns null when Limelight has no target`() {
        whenever(mockPoseProvider.getTagPosition()).thenReturn(null)
        whenever(mockTimer.get()).thenReturn(2.0)

        AutoAlignCalc.poseProvider = mockPoseProvider

        val result = AutoAlignCalc.getAlignSpeeds(0.0, 0.0, mockTimer)
        assertNull(result)
    }

    @Test
    fun `getAlignSpeeds returns correct ChassisSpeeds when Limelight has target`() {
        val mockPose = Pose3d(
            Translation3d(
                0.0, 0.0, 1.0
            ),
            Rotation3d(
                0.0, 0.0, 0.0
            )
        )
        whenever(mockPoseProvider.getTagPosition()).thenReturn(mockPose)
        whenever(mockTimer.get()).thenReturn(2.0)

        AutoAlignCalc.poseProvider = mockPoseProvider

        val result = AutoAlignCalc.getAlignSpeeds(0.0, 0.0, mockTimer)
        assertNotNull(result)
        assertEquals(1.0, result!!.vxMetersPerSecond)
        assertEquals(0.0, result.vyMetersPerSecond)
    }
}