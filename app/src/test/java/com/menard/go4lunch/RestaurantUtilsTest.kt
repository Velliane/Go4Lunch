package com.menard.go4lunch

import android.content.Context
import android.location.Location
import com.menard.go4lunch.utils.distanceToUser
import com.menard.go4lunch.utils.getOpeningHours
import com.menard.go4lunch.utils.setRating
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class RestaurantUtilsTest {

    private lateinit var context: Context


    @Before
    fun init(){
        context = RuntimeEnvironment.application.applicationContext
    }

    @Test
    fun testSetRating(){
        assertEquals(0, setRating(0.0))
        assertEquals(1, setRating(1.7))
        assertEquals(2, setRating(3.2))
        assertEquals(3, setRating(4.8))
    }

    @Test
    fun testCloseDistanceToUser(){
        val userLocation = Location("")
        userLocation.latitude = 46.6286761
        userLocation.longitude = 5.2374655
        val location = Location("")
        location.latitude = 46.629843
        location.longitude = 5.226084

        assertEquals( "881m", distanceToUser(location, userLocation))
    }

    @Test
    fun testFarDistanceToUser(){
        val userLocation = Location("")
        userLocation.latitude = 46.6286761
        userLocation.longitude = 5.2374655
        val location = Location("")
        location.latitude = 46.6301028
        location.longitude = 5.223020300000001

        assertEquals( "1,12km", distanceToUser(location, userLocation))
    }

    @Test
    fun testGetOpeningHours(){
        val list = ArrayList<String>()
        val monday = "Monday:9:00 AM - 18:00 PM"
        val tuesday = "Tuesday:8:30 AM - 12:00 AM, 14:00 PM - 17:30 PM"
        val friday = "Friday:closed"
        list.add(monday)
        list.add(tuesday)
        list.add(friday)

        assertEquals("9:00 AM - 18:00 PM", getOpeningHours(0, list, context))
    }

}