package frc.robot.subsystems

import com.fasterxml.jackson.databind.DeserializationFeature
import frc.robot.Constants
import com.fasterxml.jackson.databind.ObjectMapper
import edu.wpi.first.math.geometry.Pose3d
import frc.robot.LimelightHelpers.LimelightResults
import java.net.URL

object LimelightSubsystem {
    private var failed: Boolean = false

    private fun parseJson(json: String): LimelightResults? {
        val mapper: ObjectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return try {
            mapper.readValue(json, LimelightResults::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getTagPosition(): Pose3d? {
        if (failed) {
            return null
        }

        try {
            val connection =
                URL("${Constants.LimelightConstants.IP_ADDR}/results").openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = Constants.LimelightConstants.TIMEOUT

            val pingResult = connection.inputStream.bufferedReader().use { it.readText() }

            val results = parseJson(pingResult)

            if (results == null) {
                failed = true
                return null
            }

            return results.targets_Fiducials[0].targetPose_RobotSpace
        } catch (e: Exception) {
            failed = true
            return null
        }
    }
}