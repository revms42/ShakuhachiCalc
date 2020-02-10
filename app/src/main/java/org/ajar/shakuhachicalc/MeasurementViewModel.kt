package org.ajar.shakuhachicalc

import androidx.lifecycle.*
import kotlin.math.roundToLong

class NumberTransformBinding {
    val text = MutableLiveData<String>()
    val number = Transformations.map(text) { if(it.isNotEmpty()) it.toDouble().roundToLong() else 0 }

    init {
        number.observeForever { if(it.toString() != text.value) text.value = it.toString() }
    }
}

class MeasurementViewModel : ViewModel() {
    val topBore = NumberTransformBinding()
    val bottomBore = NumberTransformBinding()
    val length = NumberTransformBinding()
    val wallThickness = NumberTransformBinding()
    val utaguchiWidth: LiveData<String>
    val utaguchiHeight: LiveData<String>
    val fingerHoleDiameter: LiveData<String>
    val earRatio = MediatorLiveData<String>()
    val nearestNote: LiveData<String>
    val holeOne = MediatorLiveData<String>()
    val holeTwo = MediatorLiveData<String>()
    val holeThree = MediatorLiveData<String>()
    val holeFour = MediatorLiveData<String>()
    val holeFive = MediatorLiveData<String>()

    val isBright = MutableLiveData<Boolean>()
    val useNavaching = MutableLiveData<Boolean>()

    var calculation: CalculationDesc = Calculation.Bright

    private val averageBore: Long
        get() {
            val sum = (topBore.number.value?: 0L) + (bottomBore.number.value?: 0L)
            return if(sum != 0L) {
                sum / 2L
            } else {
                0L
            }
        }

    init {
        isBright.observeForever {
            calculation = if(it) Calculation.Dark else Calculation.Bright
            length.text.value = calculation.lengthFromBore(averageBore.toDouble()).toString()
        }
        utaguchiWidth = Transformations.map(topBore.number) { Calculation.utaguchiWidth(it.toDouble()).toString() }
        utaguchiHeight = Transformations.map(topBore.number) { Calculation.utaguchiHeight(it.toDouble()).toString() }
        fingerHoleDiameter = Transformations.map(length.number) { calculation.holeFromLength(it.toDouble()).toString() }
        earRatio.addSource(length.number) {
            earRatio.value = Calculation.computeEAR(averageBore.toDouble(), it.toDouble()).toString()
        }
        earRatio.addSource(topBore.number) {
            earRatio.value = Calculation.computeEAR(averageBore.toDouble(), length.number.value?.toDouble()?: 0.0).toString()
        }
        earRatio.addSource(bottomBore.number) {
            earRatio.value = Calculation.computeEAR(averageBore.toDouble(), length.number.value?.toDouble()?: 0.0).toString()
        }
        nearestNote = Transformations.map(length.number) { Calculation.findKey(it.toDouble()) }

        createHoleBinding(1, holeOne)
        createHoleBinding(2, holeTwo)
        createHoleBinding(3, holeThree)
        createHoleBinding(4, holeFour)
        createHoleBinding(5, holeFive)
    }

    private fun createHoleBinding(pos: Int, hole: MediatorLiveData<String>) {
        hole.addSource(length.number) { length ->
            if(wallThickness.number.value != null) {
                hole.value = writeHolePositionLine(
                    useNavaching.value?: false,
                    pos,
                    length.toDouble(),
                    averageBore.toDouble(),
                    fingerHoleDiameter.value!!.toDouble(),
                    wallThickness.number.value!!.toDouble()
                )
            }
        }
        hole.addSource(topBore.number) {
            if(wallThickness.number.value != null && length.number.value != null) {
                hole.value = writeHolePositionLine(
                    useNavaching.value?: false,
                    pos,
                    length.number.value!!.toDouble(),
                    averageBore.toDouble(),
                    fingerHoleDiameter.value!!.toDouble(),
                    wallThickness.number.value!!.toDouble()
                )
            }
        }
        hole.addSource(bottomBore.number) {
            if(wallThickness.number.value != null && length.number.value != null) {
                hole.value = writeHolePositionLine(
                    useNavaching.value?: false,
                    pos,
                    length.number.value!!.toDouble(),
                    averageBore.toDouble(),
                    fingerHoleDiameter.value!!.toDouble(),
                    wallThickness.number.value!!.toDouble()
                )
            }
        }
        hole.addSource(wallThickness.number) { wallThickness ->
            if(length.number.value != null) {
                hole.value = writeHolePositionLine(
                    useNavaching.value?: false,
                    pos,
                    length.number.value!!.toDouble(),
                    averageBore.toDouble(),
                    fingerHoleDiameter.value!!.toDouble(),
                    wallThickness.toDouble()
                )
            }
        }
        hole.addSource(useNavaching) {
            if(wallThickness.number.value != null && length.number.value != null) {
                hole.value = writeHolePositionLine(
                    it,
                    pos,
                    length.number.value!!.toDouble(),
                    averageBore.toDouble(),
                    fingerHoleDiameter.value!!.toDouble(),
                    wallThickness.number.value!!.toDouble()
                )
            }
        }
    }

    companion object {
        private fun writeHolePositionLine(navaching: Boolean, pos: Int, length: Double, bore: Double, hole: Double, wall: Double) : String {
            val number = if(navaching) {
                Calculation.computeHolePositionNavaching(pos, length, hole, bore, wall)
            } else {
                Calculation.computeHolePosition10ths(pos, length)
            }

            return if(number.isFinite()) number.toString() else "NaN"
        }
    }
}