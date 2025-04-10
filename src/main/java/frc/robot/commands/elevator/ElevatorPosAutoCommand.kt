package frc.robot.commands.elevator

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.ElevatorSubsystem
import kotlin.math.abs

class ElevatorPosAutoCommand (
    private val targetPosition: Double,
) : Command() {
    init {
        addRequirements(ElevatorSubsystem)
    }

    override fun execute() {
        ElevatorSubsystem.setPosition(targetPosition)
    }

    override fun isFinished(): Boolean {
        return abs(ElevatorSubsystem.getPosition() - targetPosition) < 5
    }
}
