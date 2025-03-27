package frc.robot.subsystems

import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.hardware.CANcoder
import com.ctre.phoenix6.controls.VelocityVoltage
import com.ctre.phoenix6.controls.PositionVoltage
import com.ctre.phoenix6.signals.NeutralModeValue
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue
import com.ctre.phoenix6.signals.InvertedValue
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.kinematics.SwerveModulePosition

import frc.robot.Constants.ModuleConstants

import kotlin.math.PI


class TalonSwerveModule(
    driveMotorId: Int,
    turnMotorId: Int,
    encoderId: Int,
    private val offset: Double
) {
    private val driveMotor = TalonFX(driveMotorId)
    private val turnMotor = TalonFX(turnMotorId)
    private val encoder = CANcoder(encoderId)
    private var desiredState = SwerveModuleState(0.0, Rotation2d(encoder.position.valueAsDouble + offset))

    init {
        val driveMotorConfig = TalonFXConfiguration().apply { 
            CurrentLimits.apply {
                SupplyCurrentLimitEnable = true
                SupplyCurrentLimit = ModuleConstants.DRIVING_MOTOR_CURRENT_LIMIT
            }

            Slot0.apply {
                kP = ModuleConstants.DRIVING_P
                kI = ModuleConstants.DRIVING_I
                kD = ModuleConstants.DRIVING_D
                kS = ModuleConstants.DRIVING_FF
                kV = ModuleConstants.DRIVING_V
                kA = ModuleConstants.DRIVING_A
            }

            OpenLoopRamps.apply {
                DutyCycleOpenLoopRampPeriod = 0.0
            }

            ClosedLoopRamps.apply {
                DutyCycleClosedLoopRampPeriod = 0.0
            }

            MotorOutput.apply {
                NeutralMode = NeutralModeValue.Brake
            }

            Feedback.apply {
                SensorToMechanismRatio = ModuleConstants.DRIVE_RATIO
            }
         }
        
        driveMotor.configurator.apply(driveMotorConfig)

        val turnMotorConfig = TalonFXConfiguration().apply {
            CurrentLimits.apply {
                SupplyCurrentLimitEnable = true
                SupplyCurrentLimit = ModuleConstants.TURNING_MOTOR_CURRENT_LIMIT
            }

            Feedback.apply {
                SensorToMechanismRatio = 1.0
                FeedbackRemoteSensorID = encoderId
                FeedbackSensorSource = FeedbackSensorSourceValue.RemoteCANcoder
            }
            
            ClosedLoopGeneral.apply {
                ContinuousWrap = true
            }

            Slot0.apply {
                kP = ModuleConstants.TURNING_P
                kI = ModuleConstants.TURNING_I
                kD = ModuleConstants.TURNING_D
                kS = ModuleConstants.TURNING_FF
            }

            OpenLoopRamps.apply {
                DutyCycleOpenLoopRampPeriod = 0.0
            }

            ClosedLoopRamps.apply {
                DutyCycleClosedLoopRampPeriod = 0.0
            }

            MotorOutput.apply {
                NeutralMode = NeutralModeValue.Brake
                Inverted = InvertedValue.Clockwise_Positive
            }

        }

        turnMotor.configurator.apply(turnMotorConfig)

        driveMotor.setPosition(0.0)
    }

    fun getState(): SwerveModuleState {
        return SwerveModuleState(
            driveMotor.velocity.valueAsDouble,
            Rotation2d(turnMotor.position.valueAsDouble * 2 * PI + offset)
        )
    }

    fun getPosition(): SwerveModulePosition {
        return SwerveModulePosition(
            driveMotor.position.valueAsDouble,
            Rotation2d(turnMotor.position.valueAsDouble * 2 * PI + offset)
        )
    }

    fun setDesiredState(desiredState: SwerveModuleState) {
        val correctedState = SwerveModuleState(
            desiredState.speedMetersPerSecond,
            Rotation2d(desiredState.angle.radians - this.offset)
        )
        correctedState.optimize(Rotation2d(encoder.position.valueAsDouble * 2 * PI))

        driveMotor.setControl(VelocityVoltage(correctedState.speedMetersPerSecond))
        turnMotor.setControl(PositionVoltage(correctedState.angle.radians / (2 * PI)))

        this.desiredState = desiredState
    }

    fun resetEncoders() {
        driveMotor.setPosition(0.0)
    }
}
