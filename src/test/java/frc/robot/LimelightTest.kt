// TODO: Test for race conditions and coroutine cancellation

package frc.robot

import frc.robot.interfaces.LimelightService
import frc.robot.subsystems.LimelightSubsystem
import kotlinx.coroutines.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

internal class LimelightTest {
    private val mockLimelightService: LimelightService = mock()
    private lateinit var originalService: LimelightService

    @BeforeEach
    fun setup() {
        originalService = LimelightSubsystem.limelightService
        LimelightSubsystem.coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    }

    @AfterEach
    fun tearDown() {
        LimelightSubsystem.limelightService = originalService
        LimelightSubsystem.coroutineScope.cancel()
    }

    @Test
    fun `parseJson returns null when Limelight has no target`() {
        val result = LimelightSubsystem.parseJson(null)
        assertNull(result)
    }

    @Test
    fun `parseJson returns null when a bad string is given`() {
        val result = LimelightSubsystem.parseJson("bad string")
        assertNull(result)
    }

    @Test
    fun `parseJson returns a valid Pose3d when a valid pose is given`() {
        val validJson = """
            {
            "Classifier": [],
            "Detector": [],
            "Fiducial": [
            {
                "fID": 2,
                "fam": "16H5C",
                "pts": [],
                "skew": [],
                "t6c_ts": [
                0.33247368976801916,
                -0.05672695778305914,
                -2.5042031405987144,
                -4.680849607956358,
                -5.171154989721864,
                4.528697946312339
                ],
                "t6r_fs": [
                4.738896418276903,
                -1.5926603672041666,
                0.5194469577830592,
                4.522658587661256,
                4.258580454853879,
                5.5236539893713275
                ],
                "t6r_ts": [
                0.33247368976801916,
                -0.05672695778305914,
                -2.5042031405987144,
                -4.680849607956358,
                -5.171154989721864,
                4.528697946312339
                ],
                "t6t_cs": [
                -0.09991902572799474,
                -0.1234042720218289,
                2.5218203039582496,
                4.278368708252767,
                5.508508005282244,
                -4.1112864453027775
                ],
                "t6t_rs": [
                -0.09991902572799474,
                -0.1234042720218289,
                2.5218203039582496,
                4.278368708252767,
                5.508508005282244,
                -4.1112864453027775
                ],
                "ta": 0.005711808800697327,
                "tx": -2.0525293350219727,
                "txp": 149.4874725341797,
                "ty": 2.7294836044311523,
                "typ": 107.14710235595703
            }
            ],
            "Retro": [],
            "pID": 0,
            "tl": 19.78130340576172,
            "ts": 3284447.910569,
            "v": 1
        }
        """

        val result = LimelightSubsystem.parseJson(validJson)
        assert(result != null)
        assertEquals( -0.09991902572799474, result!!.targets_Fiducials?.get(0)?.targetPose_RobotSpace?.x)
        assertEquals(-0.1234042720218289, result.targets_Fiducials?.get(0)?.targetPose_RobotSpace?.y)
        assertEquals(2.5218203039582496, result.targets_Fiducials?.get(0)?.targetPose_RobotSpace?.z)
    }

    @Test
    fun `parseJson returns an empty limelight result when a valid empty string is given`() {
        val validEmptyJson = """
            {
            "Classifier": [],
            "Detector": [],
            "Fiducial": [],
            "Retro": [],
            "pID": 0,
            "tl": 19.78130340576172,
            "ts": 3284447.910569,
            "v": 1
        }
        """

        val result = LimelightSubsystem.parseJson(validEmptyJson)
        assert(result != null)
        assertEquals(0, result!!.targets_Fiducials?.size)
    }

    @Test
    fun `updateTagPosition sets tagPose correctly when Limelight has no target`() = runBlocking {
        val validEmptyJson = """
            {
            "Classifier": [],
            "Detector": [],
            "Fiducial": [],
            "Retro": [],
            "pID": 0,
            "tl": 19.78130340576172,
            "ts": 3284447.910569,
            "v": 1
        }
        """

        whenever(mockLimelightService.fetchResults()).thenReturn(validEmptyJson)
        LimelightSubsystem.limelightService = mockLimelightService

        LimelightSubsystem.updateTagPosition()

        assertNull(LimelightSubsystem.tagPose)
    }

    @Test
    fun `updateTagPosition sets tagPose correctly when Limelight has target`() = runBlocking {
        val validJson = """
            {
            "Classifier": [],
            "Detector": [],
            "Fiducial": [
            {
                "fID": 2,
                "fam": "16H5C",
                "pts": [],
                "skew": [],
                "t6c_ts": [
                0.33247368976801916,
                -0.05672695778305914,
                -2.5042031405987144,
                -4.680849607956358,
                -5.171154989721864,
                4.528697946312339
                ],
                "t6r_fs": [
                4.738896418276903,
                -1.5926603672041666,
                0.5194469577830592,
                4.522658587661256,
                4.258580454853879,
                5.5236539893713275
                ],
                "t6r_ts": [
                0.33247368976801916,
                -0.05672695778305914,
                -2.5042031405987144,
                -4.680849607956358,
                -5.171154989721864,
                4.528697946312339
                ],
                "t6t_cs": [
                -0.09991902572799474,
                -0.1234042720218289,
                2.5218203039582496,
                4.278368708252767,
                5.508508005282244,
                -4.1112864453027775
                ],
                "t6t_rs": [
                -0.09991902572799474,
                -0.1234042720218289,
                2.5218203039582496,
                4.278368708252767,
                5.508508005282244,
                -4.1112864453027775
                ],
                "ta": 0.005711808800697327,
                "tx": -2.0525293350219727,
                "txp": 149.4874725341797,
                "ty": 2.7294836044311523,
                "typ": 107.14710235595703
            }
            ],
            "Retro": [],
            "pID": 0,
            "tl": 19.78130340576172,
            "ts": 3284447.910569,
            "v": 1
        }
        """
        whenever(mockLimelightService.fetchResults()).thenReturn(validJson)
        LimelightSubsystem.limelightService = mockLimelightService

        LimelightSubsystem.updateTagPosition()
        val tagPose = LimelightSubsystem.tagPose
        assert(tagPose != null)
        assertEquals(-0.09991902572799474, tagPose!!.x)
    }

}