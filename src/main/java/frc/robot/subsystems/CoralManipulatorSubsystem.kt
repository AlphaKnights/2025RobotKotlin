package frc.robot.subsystems

import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.signals.NeutralModeValue
import edu.wpi.first.wpilibj.Ultrasonic
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.robot.Constants

object CoralManipulatorSubsystem : SubsystemBase() {
    private val rangeFinder = Ultrasonic(
        Constants.UltrasonicConstants.PING_CHANNEL,
        Constants.UltrasonicConstants.ECHO_CHANNEL
    )
    private val launchMotor = TalonFX(Constants.LaunchConstants.MOTOR_ID)

    init {
        Ultrasonic.setAutomaticMode(true)
        rangeFinder.isEnabled = true

        val launchMotorConfig = TalonFXConfiguration().apply {
            MotorOutput.apply {
                NeutralMode = NeutralModeValue.Brake
            }
        }

        launchMotor.configurator.apply(launchMotorConfig)
    }

    fun forward() {
        launchMotor.set(Constants.LaunchConstants.LAUNCH_SPEED)
    }

    fun stop() {
        launchMotor.stopMotor()
    }

    fun coralInside(): Boolean {
        return rangeFinder.rangeInches < Constants.UltrasonicConstants.CORAL_DISTANCE
    }
}