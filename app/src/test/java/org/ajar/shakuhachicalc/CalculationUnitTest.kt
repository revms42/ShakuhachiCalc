package org.ajar.shakuhachicalc

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CalculationUnitTest {

    @Test
    fun testHolePlacement() {
        assertEquals(-1.0, Calculation.computeHolePosition(0, 545.0), 0.01)
        assertEquals(121.0, Calculation.computeHolePosition(1, 545.0), 0.01)
        assertEquals(175.5, Calculation.computeHolePosition(2, 545.0), 0.01)
        assertEquals(230.0, Calculation.computeHolePosition(3, 545.0), 0.01)
        assertEquals(284.5, Calculation.computeHolePosition(4, 545.0), 0.01)
        assertEquals(320.5, Calculation.computeHolePosition(5, 545.0), 0.01)
        assertEquals(-1.0, Calculation.computeHolePosition(6, 545.0), 0.01)
    }
}
