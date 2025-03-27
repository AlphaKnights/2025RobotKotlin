package frc.robot

object Units {
    private const val METERS_PER_INCH = 0.0254

    fun inchesToMeters(inches: Double): Double {
        return inches * METERS_PER_INCH
    }


}