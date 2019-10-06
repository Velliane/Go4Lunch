package com.menard.go4lunch

import com.menard.go4lunch.utils.*
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.DayOfWeek

class DateUtilsTest {

    @Test
    fun testMessageDateToDateOnly() {
        val date = "2019-09-20T16:33:15"

        val changedDate = parseMessageDateToDateOnly(date)

        assertEquals("20/09/19", changedDate)
    }

    @Test
    fun testMessageDateToHoursOnly(){
        val date = "2019-09-20T16:33:15"

        val changedDate = parseMessageDateToHoursOnly(date)

        assertEquals("16:33:15", changedDate)
    }

    @Test
    fun testGetNumberOfDay(){
        val dayOne: DayOfWeek = DayOfWeek.MONDAY
        val dayTwo: DayOfWeek = DayOfWeek.THURSDAY

        val numberOfDayOne: Int = getNumberOfDay(dayOne)
        val numberOfDayTwo: Int = getNumberOfDay(dayTwo)

        assertEquals(1, numberOfDayOne)
        assertEquals(4, numberOfDayTwo)
    }

    @Test
    fun testShowIfOpenOrNot(){
        assertEquals("Open", checkIfOpen(true))
        assertEquals("Close", checkIfOpen(false))
    }

    @Test
    fun testParsePeriodHours(){
        assertEquals("09:00", parsePeriodHoursToHours("0900"))
    }

}