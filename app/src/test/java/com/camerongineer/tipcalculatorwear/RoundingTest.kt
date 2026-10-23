package com.camerongineer.tipcalculatorwear

import com.camerongineer.tipcalculatorwear.utils.roundDown
import com.camerongineer.tipcalculatorwear.utils.roundUp
import junit.framework.TestCase.assertEquals
import org.junit.Test

class RoundingTest {

    @Test
    fun `5023 rounded up to nearest 50`() {
        assertEquals(5050, roundUp(5023, 50))
    }

    @Test
    fun `5045 rounded up to nearest 50`() {
        assertEquals(5050, roundUp(5045, 50))
    }

    @Test
    fun `5050 rounded up to nearest 50`() {
        assertEquals(5100, roundUp(5050, 50))
    }

    @Test
    fun `5080 rounded up to nearest 50`() {
        assertEquals(5100, roundUp(5080, 50))
    }

    @Test
    fun `5100 rounded up to nearest 100`() {
        assertEquals(5200, roundUp(5100, 100))
    }

    @Test
    fun `5150 rounded up to nearest 100`() {
        assertEquals(5200, roundUp(5150, 100))
    }

    @Test
    fun `5200 rounded up to nearest 100`() {
        assertEquals(5300, roundUp(5200, 100))
    }

    @Test
    fun `5300 rounded up to nearest 250`() {
        assertEquals(5500, roundUp(5300, 250))
    }

    @Test
    fun `5400 rounded up to nearest 250`() {
        assertEquals(5500, roundUp(5400, 250))
    }

    @Test
    fun `5550 rounded up to nearest 250`() {
        assertEquals(5750, roundUp(5550, 250))
    }

    @Test
    fun `5700 rounded up to nearest 500`() {
        assertEquals(6000, roundUp(5700, 500))
    }

    @Test
    fun `5900 rounded up to nearest 500`() {
        assertEquals(6000, roundUp(5900, 500))
    }

    @Test
    fun `6000 rounded up to nearest 500`() {
        assertEquals(6500, roundUp(6000, 500))
    }

    @Test
    fun `5023 rounded down to nearest 50`() {
        assertEquals(5000, roundDown(5023, 50))
    }

    @Test
    fun `5050 rounded down to nearest 50`() {
        assertEquals(5000, roundDown(5050, 50))
    }

    @Test
    fun `5100 rounded down to nearest 50`() {
        assertEquals(5050, roundDown(5100, 50))
    }

    @Test
    fun `5100 rounded down to nearest 100`() {
        assertEquals(5000, roundDown(5100, 100))
    }

    @Test
    fun `5150 rounded down to nearest 100`() {
        assertEquals(5100, roundDown(5150, 100))
    }

    @Test
    fun `5200 rounded down to nearest 100`() {
        assertEquals(5100, roundDown(5200, 100))
    }

    @Test
    fun `5550 rounded down to nearest 250`() {
        assertEquals(5500, roundDown(5550, 250))
    }

    @Test
    fun `5700 rounded down to nearest 250`() {
        assertEquals(5500, roundDown(5700, 250))
    }

    @Test
    fun `5750 rounded down to nearest 250`() {
        assertEquals(5500, roundDown(5750, 250))
    }

    @Test
    fun `5900 rounded down to nearest 500`() {
        assertEquals(5500, roundDown(5900, 500))
    }

    @Test
    fun `6000 rounded down to nearest 500`() {
        assertEquals(5500, roundDown(6000, 500))
    }

    @Test
    fun `0 rounded up to nearest 50`() {
        assertEquals(50, roundUp(0, 50))
    }

    @Test
    fun `0 rounded down to nearest 50 falls below zero and is clamped by the view model`() {
        assertEquals(-50, roundDown(0, 50))
    }

    @Test
    fun `49 rounded up to nearest 50`() {
        assertEquals(50, roundUp(49, 50))
    }

    @Test
    fun `49 rounded down to nearest 50`() {
        assertEquals(0, roundDown(49, 50))
    }

    @Test
    fun `1249 rounded up to nearest 500`() {
        assertEquals(1500, roundUp(1249, 500))
    }

    @Test
    fun `1249 rounded down to nearest 500`() {
        assertEquals(1000, roundDown(1249, 500))
    }

    @Test
    fun `16400 rounded up to nearest 500`() {
        assertEquals(16500, roundUp(16400, 500))
    }

    @Test
    fun `49999 rounded down to nearest 500`() {
        assertEquals(49500, roundDown(49999, 500))
    }

    @Test
    fun `500 rounded down to nearest 50`() {
        assertEquals(450, roundDown(500, 50))
    }

    @Test
    fun `1234567 rounded up to nearest 250`() {
        assertEquals(1234750, roundUp(1234567, 250))
    }

    @Test
    fun `1234567 rounded down to nearest 250`() {
        assertEquals(1234500, roundDown(1234567, 250))
    }
}