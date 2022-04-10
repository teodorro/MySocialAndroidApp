package com.example.mysocialandroidapp.util

import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

object DateStringFormatter {
    private var dateTimeFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    private var dateFormatter = SimpleDateFormat("dd.MM.yyyy")
    private var timeFormatter = SimpleDateFormat("HH:mm")
    private var backDateTimeFormatter = DateTimeFormatter.ofPattern( "dd.MM.yyyy HH:mm:ss")
    private var backDateFormatter = DateTimeFormatter.ofPattern( "dd.MM.yyyy")

    fun getDateTimeFromInstance(date: String): String {
        return dateTimeFormatter.format(Date.from(Instant.parse(date)))
    }

    fun getDateFromInstance(date: String): String {
        return dateFormatter.format(Date.from(Instant.parse(date)))
    }

    fun getTimeFromInstance(date: String): String {
        return timeFormatter.format(Date.from(Instant.parse(date)))
    }

    fun getEpochFromSimpleDate(date: String): String{
        return LocalDate.parse(date, backDateFormatter)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant().epochSecond
            .toString()
    }

    fun getZonedDateTimeFromSimpleString(date: String): ZonedDateTime{
        return LocalDate.parse(date, backDateFormatter)
            .atStartOfDay(ZoneId.systemDefault())
    }

    fun getZonedDateTimeFromDateTimeString(date: String): ZonedDateTime{
        return LocalDateTime.parse(date, backDateTimeFormatter)
            .atZone(ZoneId.systemDefault())
    }

    fun getEpochFromDateTime(date: String): String{
        return LocalDateTime.parse(date, backDateTimeFormatter)
            .atZone(ZoneId.systemDefault())
            .toInstant().epochSecond
            .toString()
    }

//    fun getEpochFromDateTime(date: String): String {
//        if (date.contains("T"))
//            return date
//        else
//        return LocalDateTime.parse(date, backDateTimeFormatter)
//            .atZone(ZoneId.systemDefault())
//            .toInstant()
//            .toString()
//    }
}