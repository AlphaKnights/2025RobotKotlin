/*
 * (C) 2025 Galvaknights
 */
package frc.robot

import edu.wpi.first.math.geometry.Pose3d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import frc.robot.subsystems.AutoAlignCalc
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class AutoAlignTest {
    @Test
    fun `getAlignSpeeds moves left when tag is to the left`() {
        // Robot at (0, 0, 0), tag perceived at (0, 1, 0) (1m left)
        val pose = Pose3d(Translation3d(0.0, 0.0, 0.0), Rotation3d())
        val result = AutoAlignCalc.getAlignSpeeds(0.0, 1.0, pose)

        // Y-speed should be negative (left in WPILib coordinates)
        assertTrue(result.vxMetersPerSecond > 0.0)
        assertEquals(0.0, result.vyMetersPerSecond, 0.001)
        assertEquals(0.0, result.omegaRadiansPerSecond, 0.001)
    }

    @Test
    fun `getAlignSpeeds moves forward when behind tag`() {
        // Robot at (0, 0, 2m), tag perceived at (0, 0, 0)
        val pose = Pose3d(Translation3d(0.0, 0.0, 2.0), Rotation3d())
        val result = AutoAlignCalc.getAlignSpeeds(0.0, 0.0, pose)

        // X-speed should be positive (forward)
        assertTrue(result.vxMetersPerSecond > 0.0)
        assertEquals(0.0, result.vyMetersPerSecond, 0.001)
        assertEquals(0.0, result.omegaRadiansPerSecond, 0.001)
    }

    @Test
    fun `getAlignSpeeds rotates CCW when facing right of tag`() {
        // Robot rotated +90 degrees (facing right)
        val pose = Pose3d(Translation3d(), Rotation3d(0.0, Math.PI / 2, 0.0))
        val result = AutoAlignCalc.getAlignSpeeds(0.0, 0.0, pose)

        // Omega should be negative (CCW rotation to align)
        assertTrue(result.omegaRadiansPerSecond < 0.0)
        assertEquals(0.0, result.vxMetersPerSecond, 0.001)
        assertEquals(0.0, result.vyMetersPerSecond, 0.001)
    }

    @Test
    fun `getAlignSpeeds stops when within deadzone`() {
        val pose =
            Pose3d(
                Translation3d(
                    Constants.AlignConstants.ALIGN_DEADZONE - 0.01,
                    0.0,
                    Constants.AlignConstants.ALIGN_DEADZONE - 0.01,
                ),
                Rotation3d(0.0, 0.0, Constants.AlignConstants.ALIGN_ROT_DEADZONE - 0.01),
            )

        val result = AutoAlignCalc.getAlignSpeeds(0.0, 0.0, pose)
        assertEquals(0.0, result.vxMetersPerSecond, 0.001)
        assertEquals(0.0, result.vyMetersPerSecond, 0.001)
        assertEquals(0.0, result.omegaRadiansPerSecond, 0.001)
    }

    @Test
    fun `getAlignSpeeds returns zero speeds when within deadzone`() {
        val pose =
            Pose3d(
                Translation3d(
                    0.0,
                    0.0,
                    0.0,
                ),
                Rotation3d(
                    0.0,
                    0.0,
                    0.0,
                ),
            )
        val expectedChassisSpeeds =
            ChassisSpeeds(0.0, -0.0, -0.0)

        val result =
            AutoAlignCalc.getAlignSpeeds(
                0.0,
                0.0,
                pose,
            )
        assertEquals(
            expectedChassisSpeeds.vxMetersPerSecond,
            result.vxMetersPerSecond,
        )
        assertEquals(
            expectedChassisSpeeds.vyMetersPerSecond,
            result.vyMetersPerSecond,
        )
        assertEquals(
            expectedChassisSpeeds.omegaRadiansPerSecond,
            result.omegaRadiansPerSecond,
        )
    }

    @Test
    fun `getAlignSpeeds returns left when tag is to the left`() {
        val pose =
            Pose3d(
                Translation3d(
                    5.0,
                    0.0,
                    0.0,
                ),
                Rotation3d(
                    0.0,
                    0.0,
                    0.0,
                ),
            )
        val expectedChassisSpeeds =
            ChassisSpeeds(0.0, -1.0, -0.0)

        val result =
            AutoAlignCalc.getAlignSpeeds(
                0.0,
                0.0,
                pose,
            )
        assertEquals(
            expectedChassisSpeeds.vxMetersPerSecond,
            result.vxMetersPerSecond,
        )
        assertEquals(
            expectedChassisSpeeds.vyMetersPerSecond,
            result.vyMetersPerSecond,
        )
        assertEquals(
            expectedChassisSpeeds.omegaRadiansPerSecond,
            result.omegaRadiansPerSecond,
        )
    }

    @Test
    fun `getAlignSpeeds returns correct speeds when directly behind tag`() {
        val pose =
            Pose3d(
                Translation3d(
                    0.0,
                    0.0,
                    1.0,
                ),
                Rotation3d(
                    0.0,
                    0.0,
                    0.0,
                ),
            )
        val offsets = doubleArrayOf(0.0, 0.0)
        val expectedChassisSpeeds =
            ChassisSpeeds(1.0, -0.0, -0.0)

        val result =
            AutoAlignCalc.getAlignSpeeds(
                offsets[0],
                offsets[1],
                pose,
            )
        assertEquals(
            expectedChassisSpeeds.vxMetersPerSecond,
            result.vxMetersPerSecond,
        )
        assertEquals(
            expectedChassisSpeeds.vyMetersPerSecond,
            result.vyMetersPerSecond,
        )
        assertEquals(
            expectedChassisSpeeds.omegaRadiansPerSecond,
            result.omegaRadiansPerSecond,
        )
    }
}
