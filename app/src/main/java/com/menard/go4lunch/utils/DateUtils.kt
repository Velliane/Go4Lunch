package com.menard.go4lunch.utils

import com.menard.go4lunch.model.detailsrequest.ResultDetails
import org.threeten.bp.DayOfWeek
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.StringBuilder


val messageDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
val dateOnlyDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
val hoursOnlyDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
val periodDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HHmm")
val hoursHumanDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")


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

fun parseTodayHoursToPeriodHours(date: String): String{
    val dateTime: LocalDateTime = LocalDateTime.parse(date, messageDateTimeFormatter)
    return dateTime.format(periodDateTimeFormatter)
}

fun parsePeriodHoursToHours(date: String): String{
    val stringBuilder = StringBuilder()
    val int = date.length

    stringBuilder.append(date)
    stringBuilder.insert(int-2, ":")
    return  stringBuilder.toString()
}

fun setNotificationsTime(today: LocalDateTime, hour: Int, minute:Int, second:Int) : Long{
    val desiredDate = today.withHour(hour).withMinute(minute).withSecond(second)
    val duration = Duration.between(today, desiredDate).toMinutes()

    return if(duration < 0){
        duration + 1440
    }else{
        duration
    }
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




fun getFormattedOpeningHoursInfos(isOpen: Boolean): String{
    return checkIfOpen(isOpen)
}

