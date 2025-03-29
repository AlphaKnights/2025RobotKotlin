package frc.robot

import edu.wpi.first.wpilibj2.command.button.CommandJoystick
import frc.robot.commands.elevator.ElevatorManualCommand
import frc.robot.commands.elevator.ElevatorPosCommand

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
    private val driverController = DriverController
    private val buttonBoard = CommandJoystick(Constants.OperatorConstants.BUTTON_BOARD_PORT)

    init
    {
        configureBindings()

        driverController
    }

    private fun configureBindings() {
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
    }
}