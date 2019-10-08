package com.menard.go4lunch

import com.menard.go4lunch.utils.*
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime

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
    fun testParsePeriodHours(){
        assertEquals("09:00", parsePeriodHoursToHours("0900"))
        assertEquals("14:00", parsePeriodHoursToHours("1400"))
    }

    @Test
    fun testSetNotificationTime(){
        val date: LocalDateTime = LocalDateTime.now().withHour(10).withMinute(12).withSecond(0)
        assertEquals(108L, setNotificationsTime(date, 12, 0,0))
    }

}