package frc.robot.commands.elevator

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.Constants
import frc.robot.subsystems.ElevatorSubsystem

class ElevatorManualCommand (
    private val direction: Constants.ElevatorDirection
) : Command() {
    init {
        addRequirements(ElevatorSubsystem)
    }

    override fun execute() {
        val speed: Double = when (direction) {
            Constants.ElevatorDirection.UP -> Constants.ElevatorConstants.MAX_ELEVATOR_SPEED * 0.1
            Constants.ElevatorDirection.DOWN -> -Constants.ElevatorConstants.MAX_ELEVATOR_SPEED * 0.1
        }

        ElevatorSubsystem.move(speed)
    }

    override fun isFinished(): Boolean { return false }

    override fun end(interrupted: Boolean) {
        ElevatorSubsystem.stop()
    }
}
