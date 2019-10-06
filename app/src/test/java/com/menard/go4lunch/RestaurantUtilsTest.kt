package com.menard.go4lunch

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.menard.go4lunch.model.detailsrequest.Close
import com.menard.go4lunch.model.detailsrequest.DetailsRequest
import com.menard.go4lunch.model.detailsrequest.Open
import com.menard.go4lunch.model.detailsrequest.Period
import com.menard.go4lunch.utils.getClosingTimeOfDay
import com.menard.go4lunch.utils.getNumberOfDay
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.threeten.bp.DayOfWeek

@RunWith(RobolectricTestRunner::class)
class RestaurantUtilsTest {


    @Before
    fun init(){
        AndroidThreeTen.init(Application())
    }

    @Test
    fun testClosedWillOpenAt9h(){
        val isOpen = false
        val today = getNumberOfDay(DayOfWeek.MONDAY)
        val time = "0700"

        val detailsRequest = DetailsRequest()
        val open = Open(1, "0900")
        val close = Close(1, "1800")
        val period = Period(close, open)
        val periods = ArrayList<Period>()
        periods.add(period)
        detailsRequest.result?.openingHours?.periods = periods

        assertEquals("Closed, will open today at 09:00", getClosingTimeOfDay(isOpen, today, periods, time))
    }

    @Test
    fun testOpenWillCloseAt18h(){
        val isOpen = true
        val today = getNumberOfDay(DayOfWeek.MONDAY)
        val time = "1500"

        val detailsRequest = DetailsRequest()
        val open = Open(1, "0900")
        val close = Close(1, "1800")
        val period = Period(close, open)
        val periods = ArrayList<Period>()
        periods.add(period)
        detailsRequest.result?.openingHours?.periods = periods

        assertEquals("Open, will close at 18:00", getClosingTimeOfDay(isOpen, today, periods, time))
    }

    @Test
    fun testCloseWillOpenChooseMostCloserOpeningTime(){
        val isOpen = false
        val today = getNumberOfDay(DayOfWeek.MONDAY)
        val time = "1000"

        val detailsRequest = DetailsRequest()
        //-- First period --
        val open = Open(1, "0900")
        val close = Close(1, "1200")
        val period = Period(close, open)
        //-- Second period --
        val open2 = Open(1, "1400")
        val close2 = Close(1, "1900")
        val period2 = Period(close2, open2)
        val periods = ArrayList<Period>()
        periods.add(period)
        periods.add(period2)
        detailsRequest.result?.openingHours?.periods = periods

        assertEquals("Closed, will open today at 14:00", getClosingTimeOfDay(isOpen, today, periods, time))
    }
}