package frc.robot

import edu.wpi.first.wpilibj2.command.button.CommandJoystick
import frc.robot.commands.DriveCommand
import frc.robot.commands.ResetHeadingCommand
import frc.robot.commands.autoalign.AutoAlignManualCommand
import frc.robot.commands.coralmanipulator.IntakeCommand
import frc.robot.commands.coralmanipulator.LaunchCommand
import frc.robot.commands.elevator.ElevatorManualCommand
import frc.robot.commands.elevator.ElevatorPosCommand
import frc.robot.subsystems.DriveSubsystem
import frc.robot.subsystems.LimelightSubsystem

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the [Robot]
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 *
 * In Kotlin, it is recommended that all your Subsystems are Kotlin objects. As such, there
 * can only ever be a single instance. This eliminates the need to create reference variables
 * to the various subsystems in this container to pass into to commands. The commands can just
 * directly reference the (single instance of the) object.
 */
object RobotContainer
{
    private val joystickController = JoystickController()
    private val buttonBoard = CommandJoystick(Constants.OperatorConstants.BUTTON_BOARD_PORT)

    init
    {
        LimelightSubsystem.startPolling()
    }

    private fun configureBindings() {
        // Drive control
        DriveSubsystem.defaultCommand = DriveCommand(
            x = { joystickController.x() },
            y = { joystickController.y() },
            rot = { joystickController.rot() },
        )

        // Reset heading
        joystickController.heading()
            .whileTrue(
                ResetHeadingCommand()
            )

        // Auto Align
        buttonBoard.button(Constants.OperatorConstants.ALIGN_LEFT_BUTTON)
            .whileTrue(
                AutoAlignManualCommand(
                    Constants.AlignDirection.LEFT,
                )
            )

        buttonBoard.button(Constants.OperatorConstants.ALIGN_RIGHT_BUTTON)
            .whileTrue(
                AutoAlignManualCommand(
                    Constants.AlignDirection.RIGHT,
                )
            )

        // Manual elevator control
        buttonBoard.button(Constants.OperatorConstants.ELEVATOR_UP_BUTTON)
            .whileTrue(
                ElevatorManualCommand(
                    Constants.ElevatorDirection.UP
                )
            )

        buttonBoard.button(Constants.OperatorConstants.ELEVATOR_DOWN_BUTTON)
            .whileTrue(
                ElevatorManualCommand(
                    Constants.ElevatorDirection.DOWN
                )
            )

        // Elevator positioning
        buttonBoard.button(Constants.OperatorConstants.ELEVATOR_LVL_1_BUTTON)
            .onTrue(
                ElevatorPosCommand(
                    Constants.ElevatorConstants.LVL_1_HEIGHT
                )
            )

        buttonBoard.button(Constants.OperatorConstants.ELEVATOR_LVL_2_BUTTON)
            .onTrue(
                ElevatorPosCommand(
                    Constants.ElevatorConstants.LVL_2_HEIGHT
                )
            )

        buttonBoard.button(Constants.OperatorConstants.ELEVATOR_LVL_3_BUTTON)
            .onTrue(
                ElevatorPosCommand(
                    Constants.ElevatorConstants.LVL_3_HEIGHT
                )
            )

        buttonBoard.button(Constants.OperatorConstants.ELEVATOR_LVL_4_BUTTON)
            .onTrue(
                ElevatorPosCommand(
                    Constants.ElevatorConstants.LVL_4_HEIGHT
                )
            )

        // Coral Manipulator
        buttonBoard.button(Constants.OperatorConstants.DELIVERY_BUTTON)
            .onTrue(
                LaunchCommand()
            )
        buttonBoard.button(Constants.OperatorConstants.INTAKE_BUTTON)
            .onTrue(
                IntakeCommand()
            )
    }
}