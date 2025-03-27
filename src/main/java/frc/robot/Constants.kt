package frc.robot

/*
 * The Constants file provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This file should not be used for any other purpose.
 * All String, Boolean, and numeric (Int, Long, Float, Double) constants should use
 * `const` definitions. Other constant types should use `val` definitions.
 */

import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.SwerveDriveKinematics

import frc.robot.Units

object Constants
{
    object OperatorConstants
    {
        const val DRIVER_CONTROLLER_PORT = 0
        const val DRIVE_DEADBAND = 0.4
    }

    object DriveConstants {
        const val MAX_METERS_PER_SECOND = 15
        const val MAX_ANGULAR_SPEED = 20

        val TRACK_WIDTH = Units.inchesToMeters(inches=26.5)
        val WHEEL_BASE = Units.inchesToMeters(inches=26.5)

        val MODULE_POSITIONS = arrayOf(
            Translation2d(WHEEL_BASE / 2.0, TRACK_WIDTH / 2.0),
            Translation2d(WHEEL_BASE / 2.0, -TRACK_WIDTH / 2.0),
            Translation2d(-WHEEL_BASE / 2.0, TRACK_WIDTH / 2.0),
            Translation2d(-WHEEL_BASE / 2.0, -TRACK_WIDTH / 2.0)
        )

        val DRIVE_KINEMATICS = SwerveDriveKinematics(*MODULE_POSITIONS)

        val FRONT_LEFT_CHASSIS_ANGLUAR_OFFSET = Math.toRadians(-0.764892578125 * (360))
        val FRONT_RIGHT_CHASSIS_ANGLUAR_OFFSET = Math.toRadians(0.75 * (360))
        val BACK_LEFT_CHASSIS_ANGLUAR_OFFSET = Math.toRadians(0.079833984375 * (360))
        val BACK_RIGHT_CHASSIS_ANGLUAR_OFFSET = Math.toRadians(0.367919921875 * (360))

        const val FRONT_LEFT_DRIVING_ID = 5
        const val REAR_LEFT_DRIVING_ID = 7
        const val FRONT_RIGHT_DRIVING_ID = 4
        const val REAR_RIGHT_DRIVING_ID = 1

        const val FRONT_LEFT_TURNING_ID = 6
        const val REAR_LEFT_TURNING_ID = 8
        const val FRONT_RIGHT_TURNING_ID = 3
        const val REAR_RIGHT_TURNING_ID = 2

        const val FRONT_LEFT_CANCODER_ID = 3
        const val REAR_LEFT_CANCODER_ID = 4
        const val FRONT_RIGHT_CANCODER_ID = 2
        const val REAR_RIGHT_CANCODER_ID = 1

        const val GYRO_REVERSED = true
    }

    object ModuleConstants {
        const val DRIVE_RATIO = 17.326202353

        const val DRIVING_P = 0.8
        const val DRIVING_I = 0.0
        const val DRIVING_D = 0.0
        const val DRIVING_FF = 1.0
        const val DRIVING_V = 0.3
        const val DRIVING_A = 1.5
        const val DRIVING_MIN_OUTPUT = -1.0
        const val DRIVING_MAX_OUTPUT = 1.0

        const val TURNING_P = 40.0
        const val TURNING_I = 0.0
        const val TURNING_D = 0.0
        const val TURNING_FF = 0.0
        const val TURNING_MIN_OUTPUT = -1.0
        const val TURNING_MAX_OUTPUT = 1.0

        const val DRIVING_MOTOR_CURRENT_LIMIT = 40.0
        const val TURNING_MOTOR_CURRENT_LIMIT = 40.0
    }
}



