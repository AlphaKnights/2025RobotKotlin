package frc.robot

import edu.wpi.first.math.MathUtil.applyDeadband
import edu.wpi.first.wpilibj.Joystick
import frc.robot.commands.DriveCommand
import frc.robot.subsystems.DriveSubsystem

object DriverController
{
    private val swerveDrive = DriveSubsystem
    private val joystick = Joystick(Constants.OperatorConstants.DRIVER_CONTROLLER_PORT)

    fun setDefaultCommands() {
        swerveDrive.defaultCommand = DriveCommand(
            swerveDrive,
            {
                -applyDeadband(
                    joystick.getRawAxis(1),
                    Constants.OperatorConstants.DRIVE_DEADBAND
                ) * (-joystick.getRawAxis(3) + 1) / 2
            },
            {
                -applyDeadband(
                    joystick.getRawAxis(0),
                    Constants.OperatorConstants.DRIVE_DEADBAND
                ) * (-joystick.getRawAxis(3) + 1)/2
            },
            {
                -applyDeadband(
                    joystick.getRawAxis(2),
                    Constants.OperatorConstants.DRIVE_DEADBAND
                ) * (-joystick.getRawAxis(3) + 1)/2
            },
            {
                joystick.getRawButton(11)
            }
        )
    }
}