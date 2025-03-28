package frc.robot.commands

import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.Constants
import frc.robot.subsystems.DriveSubsystem

class DriveCommand(
    private val swerveDrive: DriveSubsystem,
    private val x: () -> Double,
    private val y: () -> Double,
    private val rot: () -> Double,
    private val heading: () -> Boolean
) : Command() {
    init {
        addRequirements(swerveDrive)
    }

    override fun execute() {
        super.execute()

        val heading: Boolean = heading()

        if (heading) {
            swerveDrive.zeroHeading()
        }

        swerveDrive.drive(
            ChassisSpeeds(
                x() * Constants.DriveConstants.MAX_METERS_PER_SECOND,
                y() * Constants.DriveConstants.MAX_METERS_PER_SECOND,
                rot() * Constants.DriveConstants.MAX_ANGULAR_SPEED
            ),
            fieldRelative = true,
        )
    }

    override fun end(interrupted: Boolean) {
        swerveDrive.drive(
            ChassisSpeeds(
                0.0, 0.0, 0.0
            ),
            fieldRelative = false,
        )
    }
}