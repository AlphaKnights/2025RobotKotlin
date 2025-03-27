package frc.robot

object Units {
    const val METERS_PER_INCH = 0.0254
    const val INCHES_PER_METER = 1 / METERS_PER_INCH

    const val RADIANS_PER_DEGREE = Math.PI / 180.0
    const val DEGREES_PER_RADIAN = 1 / RADIANS_PER_DEGREE
    fun metersToInches(meters: Double): Double {
        return meters * INCHES_PER_METER
    }

    fun inchesToMeters(inches: Double): Double {
        return inches * METERS_PER_INCH
    }


}