package com.menard.go4lunch.utils

import com.menard.go4lunch.model.detailsrequest.ResultDetails
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter


val messageDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
val dateOnlyDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
val hoursOnlyDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")


/**
 * Parse saved DateTime of message to date only
 */
fun parseMessageDateToDateOnly(date: String): String{
    val dateTime: LocalDateTime = LocalDateTime.parse(date, messageDateTimeFormatter)
    return dateTime.format(dateOnlyDateTimeFormatter)
}

/**
 * Parse saved DateTime of message to time only
 */
fun parseMessageDateToHoursOnly(date: String): String{
    val dateTime: LocalDateTime = LocalDateTime.parse(date, messageDateTimeFormatter)
    return dateTime.format(hoursOnlyDateTimeFormatter)
}


/**
 * Get the number of the Day
 */
fun getNumberOfDay(dayOfWeek: DayOfWeek): Int{
    return when(dayOfWeek){
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
    }
}


fun checkIfOpen(isOpen: Boolean): String{
    return if(isOpen) "Open" else "Close"
}


/**
 * Get hours of closing according to the number of day
 */
fun getClosingTimeOfDay(day: Int, result:ResultDetails, isOpen: Boolean): List<String> {
    val listPeriod = result.openingHours!!.periods
    val list = ArrayList<String>()

    // If actually open, get closing hours of day
    if(isOpen) {
        for (period in listPeriod!!) {
            if (period.close!!.day == day) {
                list.add(period.close!!.time!!)
            }
        }
    // Else get opening hours of day+1
    }else{
//        for (period in listPeriod) {
//            if (period.open.day == day+1) {
//                list.add(period.open.time)
//            }
//        }
    }
    return  list
}

fun getFormattedOpeningHoursInfos(isOpen: Boolean): String{
    return checkIfOpen(isOpen)
}

