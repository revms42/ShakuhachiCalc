package org.ajar.shakuhachicalc

import kotlin.math.pow

/**
 * Derived from https://www.scribd.com/doc/178200652/Shakuhaki-Design-Excellent-Guide-to-Flutes
 */
enum class Key(val rep: String) {
    A("An"),
    B_F("Bnb"),
    B("Bn"),
    C("Cn"),
    C_S("Cn#"),
    D("Dn"),
    E_F("Enb"),
    E("En"),
    F("Fn"),
    F_S("Fn#"),
    G("Gn"),
    G_S("Gn#");

    fun step(dir: Int) : Key {
        return when(dir) {
            0 -> this
            in 1..12 -> {
                when(this) {
                    G_S -> A.step(dir - 1)
                    else -> values()[this.ordinal + 1].step(dir - 1)
                }
            }
            in -1 downTo -12 -> {
                when(this) {
                    A -> G_S.step(dir + 1)
                    else -> values()[this.ordinal - 1].step(dir + 1)
                }
            }
            else -> throw NumberFormatException("Wrong dimension $dir")
        }
    }

    companion object {
        private const val a4 = 440.0 // Hz
        private val aConst = (2.0).pow(1.0/12.0)

        fun find(frequency: Double): String {
            var aNumber = 0.0
            if(frequency > a4) {
                while(a4 * aConst.pow(aNumber) < frequency) {
                    aNumber += 1.0
                }
            } else {
                while(a4 * aConst.pow(aNumber) > frequency) {
                    aNumber -= 1.0
                }
            }

            val octaves = aNumber.toInt() / 12
            val steps = aNumber.toInt() % 12

            val nNumber = 4 + octaves

            return A.step(steps).rep.replace("n", "$nNumber")
        }
    }
}

interface CalculationDesc {
    fun lengthFromBore(boreDia: Double) : Double
    fun boreFromLength(length: Double): Double
    fun holeFromLength(length: Double) : Double
}
sealed class Calculation : CalculationDesc {

    object Bright : Calculation() {
        override fun lengthFromBore(boreDia: Double) : Double = 17.0 * boreDia.pow(sixFifths)
        override fun boreFromLength(length: Double): Double = (length / 17.0).pow(fiveSixths)
        override fun holeFromLength(length: Double) : Double = (length / 17.0).pow(twoThirds)
    }

    object Dark : Calculation() {
        override fun lengthFromBore(boreDia: Double) : Double = boreDia.pow(sixFifths) * Math.PI.pow(2.0 * sixFifths)
        override fun boreFromLength(length: Double): Double = length.pow(fiveSixths) / Math.PI.pow(2.0)
        override fun holeFromLength(length: Double) : Double = length.pow(twoThirds) / (2.0 * Math.PI)
    }

    companion object {
        private const val sixFifths = 1.2
        private const val fourFifths = 0.8
        private const val fiveSixths = (5.0/6.0)
        private const val twoThirds = (2.0/3.0)

        private const val earConst = 2.858

        private const val holePlacementScaleConst = 545.0
        private const val bottomHoleConst = 121.0
        private const val holeToHoleConst = 10.0
        private const val hole4to5Const = 36.0
        private const val velocityOfSoundInAir = 350000.0 // mm/s

        private const val hzNumber = 165674.0

        fun computeEAR(boreDia: Double, length: Double): Double = (earConst * length.pow(fiveSixths)) / boreDia
        fun computeBoreFromEAR(ear: Double, length: Double) : Double = (earConst * length.pow(fiveSixths)) / ear
        fun computeLengthFromEAR(ear: Double, boreDia: Double) : Double = ((boreDia * ear) / earConst).pow(sixFifths)

        /**
         * length is in mm.
         */
        fun computeHolePosition10ths(position: Int, length: Double) : Double {
            return when (position) {
                1 -> (bottomHoleConst * length) / holePlacementScaleConst
                2, 3, 4 -> computeHolePosition10ths(position - 1, length) + (length / holeToHoleConst)
                5 -> computeHolePosition10ths(4, length) + ((hole4to5Const * length) / holePlacementScaleConst)
                else -> -1.0
            }
        }

        /**
         * From the Shakuhachi navaching pages
         */
        fun computeHolePositionNavaching(position: Int, length: Double, holeDia: Double, boreDia: Double, wallThickness: Double) : Double {
            if (position == 0 || position > 5) return -1.0
            val baseNote = 156521.0 / length
            val tHoleEffThick = wallThickness + (0.75 * holeDia)
            val idealLength = hzNumber / baseNote

            val mpEquivLength = idealLength - (0.3 * boreDia) - length

            var holeLoc = 0.0
            var lastLength = idealLength
            for(pos in 1..position) {
                val temp = when(pos) {
                    1 -> 3
                    2 -> 5
                    3 -> 7
                    4 -> 10
                    5 -> 12
                    else -> 1
                }

                val hZ = baseNote * 2.0.pow(temp / 12.0)
                val newLength = hzNumber / hZ
                val s = (lastLength - newLength) / 2.0
                val correctionFac = s * ((((((tHoleEffThick / s) * ((boreDia / holeDia).pow(2.0))) * 2.0) + 1.0).pow(0.5)) - 1)
                holeLoc = newLength - mpEquivLength - correctionFac

                lastLength = newLength + correctionFac
            }

            // There is additional calculation to be done for outside ergonomic limit hole locations.
            return length - holeLoc
        }

        fun utaguchiWidth(boreDia: Double) : Double = (boreDia * Math.PI) / 4.0
        fun utaguchiHeight(boreDia: Double) : Double = utaguchiWidth(boreDia) / 5.0
        fun holeFromBore(boreDia: Double) : Double = boreDia.pow(fourFifths)
        fun frequencyFromLength(length: Double) : Double = velocityOfSoundInAir/(2.0 * length)

        fun findKey(length: Double) : String {
            return Key.find(frequencyFromLength(length))
        }
    }
}