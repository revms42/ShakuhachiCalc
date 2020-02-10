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
    fun testHolePlacementIn10ths() {
        assertEquals(-1.0, Calculation.computeHolePosition10ths(0, 545.0), 0.01)
        assertEquals(121.0, Calculation.computeHolePosition10ths(1, 545.0), 0.01)
        assertEquals(175.5, Calculation.computeHolePosition10ths(2, 545.0), 0.01)
        assertEquals(230.0, Calculation.computeHolePosition10ths(3, 545.0), 0.01)
        assertEquals(284.5, Calculation.computeHolePosition10ths(4, 545.0), 0.01)
        assertEquals(320.5, Calculation.computeHolePosition10ths(5, 545.0), 0.01)
        assertEquals(-1.0, Calculation.computeHolePosition10ths(6, 545.0), 0.01)
    }

    @Test
    fun testHolePlacementNavaching() {
        assertEquals(-1.0, Calculation.computeHolePositionNavaching(0, 650.0, 10.0, 20.5, 3.375), 0.01)
        assertEquals(138.0, Calculation.computeHolePositionNavaching(1, 650.0, 10.0, 20.5, 3.375), 0.1)
        assertEquals(200.4, Calculation.computeHolePositionNavaching(2, 650.0, 10.0, 20.5, 3.375), 0.1)
        assertEquals(256.0, Calculation.computeHolePositionNavaching(3, 650.0, 10.0, 20.5, 3.375), 0.1)
        assertEquals(330.2, Calculation.computeHolePositionNavaching(4, 650.0, 10.0, 20.5, 3.375), 0.1)
        assertEquals(370.0, Calculation.computeHolePositionNavaching(5, 650.0, 10.0, 20.5, 3.375), 0.1)
        assertEquals(-1.0, Calculation.computeHolePositionNavaching(6, 650.0, 10.0, 20.5, 3.375), 1.0)
    }
}
