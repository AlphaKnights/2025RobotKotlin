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
    var coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var limelightService: LimelightService = HttpLimelightService

    init {
        // Start the coroutine to poll the Limelight
        coroutineScope.launch {
            while (true) {
                setTagPose(updateTagPosition())
                delay(Constants.LimelightConstants.POLLING_RATE)
            }
        }
    }

    suspend fun updateTagPosition(): Pose3d? = coroutineScope {
        try {
            ensureActive() // Atomic cancellation check

            val results = limelightService.fetchResults()
            if (results == null) {
                cancel("Limelight returned null results")
                return@coroutineScope null
            }

            return@coroutineScope parseJson(results)
                ?.targets_Fiducials
                ?.getOrNull(0)
                ?.targetPose_RobotSpace

        } catch (e: CancellationException) {
            throw e // Re-throw structured cancellation
        } catch (e: Exception) {
            println("Limelight update failed: ${e.message}")
            return@coroutineScope null
        }
    }

    suspend fun setTagPose(pose: Pose3d?) {
        mutex.withLock {
            _tagPose = pose
        }
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