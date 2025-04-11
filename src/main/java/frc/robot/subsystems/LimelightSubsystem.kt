package frc.robot.subsystems

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import edu.wpi.first.math.geometry.Pose3d
import frc.robot.interfaces.LimelightService
import frc.robot.Constants
import frc.robot.LimelightHelpers.LimelightResults
import frc.robot.interfaces.PoseProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object LimelightSubsystem : PoseProvider {
    private val mutex = Mutex()
    private var _tagPose: Pose3d? = null
    override val tagPose: Pose3d?
        get() = runBlocking { mutex.withLock { _tagPose } }
    var coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var limelightService: LimelightService = HttpLimelightService

    fun startPolling() {
        coroutineScope.launch {
            while (true) {
                setTagPose(updateTagPosition())
                delay(Constants.LimelightConstants.POLLING_RATE)
            }
        }
    }

    suspend fun updateTagPosition(): Pose3d? {
        // Atomic cancellation check (no race condition)
        coroutineScope.ensureActive()

        // Fetch results within the coroutine scope lifecycle
        limelightService.fetchResults()?.let { json ->
            return parseJson(json)?.targets_Fiducials?.getOrNull(0)?.targetPose_RobotSpace
        } ?: run {
//            This is still under review
//            coroutineScope.cancel("Limelight returned null results")
            return null
        }
    }

    suspend fun setTagPose(pose: Pose3d?) {
        mutex.withLock {
            _tagPose = pose
        }
    }

    fun isAligned(): Boolean {
        return tagPose != null && tagPose!!.translation.x < Constants.AlignConstants.ALIGN_DEADZONE &&
                tagPose!!.translation.y < Constants.AlignConstants.ALIGN_DEADZONE &&
                tagPose!!.translation.z < Constants.AlignConstants.ALIGN_DEADZONE &&
                tagPose!!.rotation.getAngle() < Constants.AlignConstants.ALIGN_ROT_DEADZONE
    }

    fun parseJson(json: String?): LimelightResults? {
        val checkedJson: String = json ?: return null
        val mapper: ObjectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        return try {
            mapper.readValue(checkedJson, LimelightResults::class.java)
        } catch (e: Exception) {
            null
        }
    }
}