package org.ajar.shakuhachicalc

import kotlin.math.pow

/**
 * Derived from https://www.scribd.com/doc/178200652/Shakuhaki-Design-Excellent-Guide-to-Flutes
 */
interface CalculationDesc {
    fun lengthFromBore(boreDia: Double) : Double
    fun boreFromLength(length: Double): Double
    fun holeFromLength(length: Double) : Double
}
sealed class Calculation : CalculationDesc {

    object Bright : Calculation() {
        override fun lengthFromBore(boreDia: Double) : Double = 17.0 * boreDia.pow(sixFifths)
        override fun boreFromLength(length: Double): Double = (length / 17.0).pow(fiveSixths)
        fun holeFromBore(boreDia: Double) : Double = boreDia.pow(fourFifths)
        override fun holeFromLength(length: Double) : Double = (length / 17.0).pow(twoThirds)
    }

    object Dark : Calculation() {
        override fun lengthFromBore(boreDia: Double) : Double = boreDia.pow(sixFifths) * (2.0 * Math.PI.pow(sixFifths))
        override fun boreFromLength(length: Double): Double = length.pow(fiveSixths) / Math.PI.pow(2.0)
        override fun holeFromLength(length: Double) : Double = length.pow(twoThirds) / (2.0 * Math.PI)
    }

    companion object {
        private const val sixFifths = 1.2
        private const val fourFifths = 0.8
        private const val fiveSixths = (5.0/6.0)
        private const val twoThirds = (2.0/3.0)
    }
}