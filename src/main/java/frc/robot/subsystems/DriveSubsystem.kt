/*
 * (C) 2025 Galvaknights
 */
package frc.robot.subsystems

import com.pathplanner.lib.auto.AutoBuilder
import com.pathplanner.lib.config.PIDConstants
import com.pathplanner.lib.config.RobotConfig
import com.pathplanner.lib.controllers.PPHolonomicDriveController
import com.pathplanner.lib.util.DriveFeedforwards
import com.studica.frc.AHRS
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.SwerveDriveOdometry
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.robot.Constants.DriveConstants
import frc.robot.Constants.PathPlannerConstants

object DriveSubsystem : SubsystemBase() {
    private var frontLeft: TalonSwerveModule =
        TalonSwerveModule(
            DriveConstants.FRONT_LEFT_DRIVING_ID,
            DriveConstants.FRONT_LEFT_TURNING_ID,
            DriveConstants.FRONT_LEFT_CANCODER_ID,
            DriveConstants.FRONT_LEFT_CHASSIS_ANGULAR_OFFSET,
        )
    private var frontRight: TalonSwerveModule =
        TalonSwerveModule(
            DriveConstants.FRONT_RIGHT_DRIVING_ID,
            DriveConstants.FRONT_RIGHT_TURNING_ID,
            DriveConstants.FRONT_RIGHT_CANCODER_ID,
            DriveConstants.FRONT_RIGHT_CHASSIS_ANGULAR_OFFSET,
        )
    private var rearLeft: TalonSwerveModule =
        TalonSwerveModule(
            DriveConstants.REAR_LEFT_DRIVING_ID,
            DriveConstants.REAR_LEFT_TURNING_ID,
            DriveConstants.REAR_LEFT_CANCODER_ID,
            DriveConstants.BACK_LEFT_CHASSIS_ANGULAR_OFFSET,
        )
    private var rearRight: TalonSwerveModule =
        TalonSwerveModule(
            DriveConstants.REAR_RIGHT_DRIVING_ID,
            DriveConstants.REAR_RIGHT_TURNING_ID,
            DriveConstants.REAR_RIGHT_CANCODER_ID,
            DriveConstants.BACK_RIGHT_CHASSIS_ANGULAR_OFFSET,
        )
    private var gyro: AHRS = AHRS(AHRS.NavXComType.kMXP_SPI)
    private var odometry: SwerveDriveOdometry

    private val config: RobotConfig =
        RobotConfig
            .fromGUISettings()

    init {
        gyro.enableBoardlevelYawReset(false)
        gyro.reset()

        odometry =
            SwerveDriveOdometry(
                DriveConstants.DRIVE_KINEMATICS,
                Rotation2d.fromDegrees(gyro.angle),
                arrayOf(
                    frontLeft.getPosition(),
                    frontRight.getPosition(),
                    rearLeft.getPosition(),
                    rearRight.getPosition(),
                ),
            )

        // PathPlanner auto builder
        AutoBuilder.configure(
            this::getPose,
            this::resetPose,
            this::getCurrentSpeeds,
            { speeds: ChassisSpeeds, _: DriveFeedforwards ->
                drive(speeds, fieldRelative = false)
            },
            PPHolonomicDriveController(
                // Translation PID
                PIDConstants(
                    PathPlannerConstants.TRANSLATION_P,
                    PathPlannerConstants.TRANSLATION_I,
                    PathPlannerConstants.TRANSLATION_D,
                ),
                // Rotation PID
                PIDConstants(
                    PathPlannerConstants.ROTATION_P,
                    PathPlannerConstants.ROTATION_I,
                    PathPlannerConstants.ROTATION_D,
                ),
                1.0,
            ),
            config,
            this::shouldFlipPath,
            this,
        )
    }

    override fun periodic() {
        // This method will be called once per scheduler run
        odometry.update(
            Rotation2d.fromDegrees(gyro.angle),
            arrayOf(
                frontLeft.getPosition(),
                frontRight.getPosition(),
                rearLeft.getPosition(),
                rearRight.getPosition(),
            ),
        )
    }

    fun getPose(): Pose2d = odometry.poseMeters

    fun resetPose(pose: Pose2d) {
        resetOdometry(pose)
    }

    // IDE bug, the detected and actual signatures are different
    @Suppress("TYPE_MISMATCH", "TOO_MANY_ARGUMENTS")
    fun getCurrentSpeeds(): ChassisSpeeds =
        DriveConstants.DRIVE_KINEMATICS.toChassisSpeeds(
            frontLeft.getState(),
            frontRight.getState(),
            rearLeft.getState(),
            rearRight.getState(),
        )

    private fun resetOdometry(pose: Pose2d) {
        odometry.resetPosition(
            Rotation2d.fromDegrees(gyro.angle),
            arrayOf(
                frontLeft.getPosition(),
                frontRight.getPosition(),
                rearLeft.getPosition(),
                rearRight.getPosition(),
            ),
            pose,
        )
    }

    fun shouldFlipPath(): Boolean =
        (DriverStation.getAlliance() ?: DriverStation.Alliance.Red) == DriverStation.Alliance.Red

    @Suppress("MagicNumber")
    fun drive(
        speeds: ChassisSpeeds,
        fieldRelative: Boolean,
    ) {
        var swerveModuleStates =
            DriveConstants.DRIVE_KINEMATICS
                .toSwerveModuleStates(
                    speeds,
                )

        if (fieldRelative) {
            swerveModuleStates =
                DriveConstants.DRIVE_KINEMATICS.toSwerveModuleStates(
                    ChassisSpeeds.fromFieldRelativeSpeeds(
                        speeds,
                        Rotation2d.fromDegrees(gyro.angle),
                    ),
                )
        }

        frontLeft.setDesiredState(swerveModuleStates[0])
        frontRight.setDesiredState(swerveModuleStates[1])
        rearLeft.setDesiredState(swerveModuleStates[2])
        rearRight.setDesiredState(swerveModuleStates[3])
    }

    /**
     * Sets the swerve modules to a specific angle for X formation.
     *
     * This is used to prevent the robot from moving.
     */
    fun setX() {
        frontLeft.setDesiredState(
            SwerveModuleState(
                0.0,
                DriveConstants.MODULE_X_ROTATIONS["frontLeft"],
            ),
        )
        frontRight.setDesiredState(
            SwerveModuleState(
                0.0,
                DriveConstants.MODULE_X_ROTATIONS["frontRight"],
            ),
        )
        rearLeft.setDesiredState(
            SwerveModuleState(
                0.0,
                DriveConstants.MODULE_X_ROTATIONS["rearLeft"],
            ),
        )
        rearRight.setDesiredState(
            SwerveModuleState(
                0.0,
                DriveConstants.MODULE_X_ROTATIONS["rearRight"],
            ),
        )
    }

    /**
     * Resets the gyro to 0 degrees.
     *
     * This is used to reset the heading of the robot.
     */
    fun zeroHeading() {
        gyro.reset()
    }
}
