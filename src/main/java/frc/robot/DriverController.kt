package frc.robot

import edu.wpi.first.math.MathUtil.applyDeadband
import edu.wpi.first.wpilibj.Joystick
import frc.robot.commands.DriveCommand
import frc.robot.subsystems.DriveSubsystem

object DriverController
{
    private val swerveDrive = DriveSubsystem
    private val joystick = Joystick(Constants.OperatorConstants.DRIVER_CONTROLLER_PORT)

    init {
        swerveDrive.defaultCommand = DriveCommand(
            x={
                -applyDeadband(
                    joystick.getRawAxis(1),
                    Constants.OperatorConstants.DRIVE_DEADBAND
                ) * (-joystick.getRawAxis(3) + 1) / 2
            },
            y={
                -applyDeadband(
                    joystick.getRawAxis(0),
                    Constants.OperatorConstants.DRIVE_DEADBAND
                ) * (-joystick.getRawAxis(3) + 1)/2
            },
            rot={
                -applyDeadband(
                    joystick.getRawAxis(2),
                    Constants.OperatorConstants.DRIVE_DEADBAND
                ) * (-joystick.getRawAxis(3) + 1)/2
            },
            heading={
                joystick.getRawButton(11)
            }
        )
    }
}