package org.ajar.shakuhachicalc

import androidx.lifecycle.*

class DoubleTransformBinding {
    val text = MutableLiveData<String>()
    val number = Transformations.map(text) { it.toDouble() }

    init {
        number.observeForever { if(it.toString() != text.value) text.value = it.toString() }
    }
}

class MeasurementViewModel : ViewModel() {
    val topBore = DoubleTransformBinding()
    val bottomBore = DoubleTransformBinding()
    val length = DoubleTransformBinding()
    val utaguchiWidth: LiveData<String>
    val utaguchiHeight: LiveData<String>
    val fingerHoleDiameter: LiveData<String>
    val earRatio = MediatorLiveData<String>()
    val holeOne: LiveData<String>
    val holeTwo: LiveData<String>
    val holeThree: LiveData<String>
    val holeFour: LiveData<String>
    val holeFive: LiveData<String>

    val isBright = MutableLiveData<Boolean>()

    var calculation: CalculationDesc = Calculation.Bright

    private val averageBore: Double
        get() {
            val sum = (topBore.number.value?: 0.0) + (bottomBore.number.value?: 0.0)
            return if(sum != 0.0) {
                sum / 2.0
            } else {
                0.0
            }
        }

    init {
        isBright.observeForever {
            calculation = if(it) Calculation.Bright else Calculation.Dark
            length.text.value = calculation.lengthFromBore(averageBore).toString()
        }
        utaguchiWidth = Transformations.map(topBore.number) { Calculation.utaguchiWidth(it).toString() }
        utaguchiHeight = Transformations.map(topBore.number) { Calculation.utaguchiHeight(it).toString() }
        fingerHoleDiameter = Transformations.map(length.number) { calculation.holeFromLength(it).toString() }
        earRatio.addSource(length.number) {
            earRatio.value = Calculation.computeEAR(averageBore, it).toString()
        }
        earRatio.addSource(topBore.number) {
            earRatio.value = Calculation.computeEAR(averageBore, length.number.value?: 0.0).toString()
        }
        earRatio.addSource(bottomBore.number) {
            earRatio.value = Calculation.computeEAR(averageBore, length.number.value?: 0.0).toString()
        }
        holeOne = Transformations.map(length.number) { Calculation.computeHolePosition(1, it).toString() }
        holeTwo = Transformations.map(length.number) { Calculation.computeHolePosition(2, it).toString() }
        holeThree = Transformations.map(length.number) { Calculation.computeHolePosition(3, it).toString() }
        holeFour = Transformations.map(length.number) { Calculation.computeHolePosition(4, it).toString() }
        holeFive = Transformations.map(length.number) { Calculation.computeHolePosition(5, it).toString() }
    }
}