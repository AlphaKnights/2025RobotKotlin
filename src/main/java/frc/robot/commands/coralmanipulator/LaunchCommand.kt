/*
 * (C) 2025 Galvaknights
 */
package frc.robot.commands.coralmanipulator

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.Constants.LaunchConstants
import frc.robot.subsystems.CoralManipulatorSubsystem

class LaunchCommand : Command() {
    private val timer = Timer()

    init {
        // each subsystem used by the command must be passed into the addRequirements() method
        addRequirements(CoralManipulatorSubsystem)

        timer.start()
    }

    override fun initialize() {
        super.initialize()
        timer.reset()
    }

    override fun execute() {
        CoralManipulatorSubsystem.forward(LaunchConstants.LAUNCH_SPEED)
    }

    override fun isFinished(): Boolean {
        // Run until the coral is out of the robot
        return (
            !CoralManipulatorSubsystem.coralInside() ||
                timer.get() > LaunchConstants.LAUNCH_TIMEOUT // seconds
        )
    }

    override fun end(interrupted: Boolean) {
        CoralManipulatorSubsystem.stop()
        super.end(interrupted)
    }
}
