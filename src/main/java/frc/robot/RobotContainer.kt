package frc.robot
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
    // Replace with CommandPS4Controller or CommandJoystick if needed
//    private val driverController = CommandXboxController(OperatorConstants.DRIVER_CONTROLLER_PORT)
    private val driverController = DriverController
    init
    {
        configureBindings()
        // Reference the Autos object so that it is initialized, placing the chooser on the dashboard
//        Autos
        driverController.setDefaultCommands()
    }

    private fun configureBindings()
    {
        // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
//        Trigger { ExampleSubsystem.exampleCondition() }.onTrue(ExampleCommand())

        // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
        // cancelling on release.
//        driverController.b().whileTrue(ExampleSubsystem.exampleMethodCommand())
    }
}