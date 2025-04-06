package frc.robot.interfaces

interface LimelightService {
    suspend fun fetchResults(): String?
}