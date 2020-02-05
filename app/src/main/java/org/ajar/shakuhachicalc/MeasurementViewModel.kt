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
    val utaguchiWidth: LiveData<String>
    val utaguchiHeight: LiveData<String>
    val fingerHoleDiameter: LiveData<String>
    val earRatio = MediatorLiveData<String>()
    val nearestNote: LiveData<String>
    val holeOne: LiveData<String>
    val holeTwo: LiveData<String>
    val holeThree: LiveData<String>
    val holeFour: LiveData<String>
    val holeFive: LiveData<String>

    val isBright = MutableLiveData<Boolean>()

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
        holeOne = Transformations.map(length.number) { Calculation.computeHolePosition(1, it.toDouble()).toString() }
        holeTwo = Transformations.map(length.number) { Calculation.computeHolePosition(2, it.toDouble()).toString() }
        holeThree = Transformations.map(length.number) { Calculation.computeHolePosition(3, it.toDouble()).toString() }
        holeFour = Transformations.map(length.number) { Calculation.computeHolePosition(4, it.toDouble()).toString() }
        holeFive = Transformations.map(length.number) { Calculation.computeHolePosition(5, it.toDouble()).toString() }
    }
}