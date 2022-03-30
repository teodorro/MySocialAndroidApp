package com.example.mysocialandroidapp.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object DateStringFormatter {
    private var formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    private var dateTimeFormatter = DateTimeFormatter.ofPattern( "dd.MM.yyyy HH:mm:ss")

    fun getSimpleFromInstance(date: String): String {
        return formatter.format(Date.from(Instant.parse(date)))
    }
    fun getInstanceFromSimple(date: String): String {
        if (date.contains("T"))
            return date
        else
        return LocalDateTime.parse(date, dateTimeFormatter)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toString()
    }
}