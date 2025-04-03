package frc.robot.subsystems

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import edu.wpi.first.math.geometry.Pose3d
import frc.robot.Constants
import frc.robot.LimelightHelpers.LimelightResults
import frc.robot.interfaces.PoseProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object LimelightSubsystem : PoseProvider {
    private val mutex = Mutex()
    private var _tagPose: Pose3d? = null
    override val tagPose: Pose3d?
        get() = runBlocking { mutex.withLock { _tagPose } }
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var limelightService: LimelightService = HttplimelightService()

    init {
        // Start the coroutine to poll the Limelight
        coroutineScope.launch {
            while (true) {
                getTagPosition()
                delay(Constants.LimelightConstants.POLLING_RATE)
            }
        }
    }

    private suspend fun getTagPosition() {
        if (!coroutineScope.isActive) return
        

        // try {
        //     // Send a GET request to the Limelight
        //     val client = HttpClient.newBuilder()
        //         .version(HttpClient.Version.HTTP_1_1)
        //         .connectTimeout(Duration.ofMillis(Constants.LimelightConstants.TIMEOUT))
        //         .build()

        //     val pingResult = withContext(Dispatchers.IO) {
        //         client.send(
        //             HttpRequest.newBuilder()
        //                 .uri(URL("${Constants.LimelightConstants.IP_ADDR}/results").toURI())
        //                 .timeout(Duration.ofMillis(Constants.LimelightConstants.TIMEOUT))
        //                 .GET()
        //                 .build(),
        //             HttpResponse.BodyHandlers.ofString()
        //         )
        //     }

        //     val results = parseJson(pingResult.body())

        //     if (results == null) {
        //         setTagPose(null)
        //         return
        //     }

        //     setTagPose(results!!.targets_Fiducials[0].targetPose_RobotSpace)
        // } catch (e: Exception) {
        //     _tagPose = null
        //     coroutineScope.cancel()
        // }
        val fetchResults: LimelightResults? = 
            parseJson(limelightService.fetchResults())
        
        setTagPose(fetchResults)
        
        if (fetchResults == null) {
            coroutineScope.cancel()
        }
    }

    private suspend fun setTagPose(pose: Pose3d?) {
        mutex.withLock {
            _tagPose = pose
        }
    }

    private fun parseJson(json: String?): LimelightResults? {
        val checkedJson: String = json ?: return null
        val mapper: ObjectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return try {
            mapper.readValue(checkedJson, LimelightResults::class.java)
        } catch (e: Exception) {
            null
        }
    }
}