package com.example.sandanalysis.util

import android.os.Build
import java.text.SimpleDateFormat
import java.util.*

val Int.string: String
    get() = toString().padStart(2, '0')

/**
 * returns [Calendar.DAY_OF_MONTH]
 * */
operator fun Calendar.component1(): Int {
    return get(Calendar.DAY_OF_MONTH)
}

/**
 * returns [Calendar.MONTH]
 * */
operator fun Calendar.component2(): Int {
    return get(Calendar.MONTH)
}

/**
 * returns [Calendar.YEAR]
 * */
operator fun Calendar.component3(): Int {
    return get(Calendar.YEAR)
}

/**
 * returns [Calendar.DAY_OF_MONTH] in string
 * */
operator fun Calendar.component4(): String {
    return component1().toString()
}

/**
 * returns [Calendar.MONTH] in string with words
 * */
operator fun Calendar.component5(): String? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) getDisplayName(
        Calendar.MONTH,
        Calendar.LONG_FORMAT,
        Locale.getDefault()
    )
    else getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
}

/**
 * returns [Calendar.YEAR] in string
 * */
operator fun Calendar.component6(): String {
    return component3().toString()
}

/**
 * returns [Calendar.HOUR_OF_DAY]
 * */
operator fun Calendar.component7(): Int {
    return get(Calendar.HOUR_OF_DAY)
}

/**
 * returns [Calendar.MINUTE]
 * */
operator fun Calendar.component8(): Int {
    return get(Calendar.MINUTE)
}

fun Calendar.hhmm(): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(time)
}

fun Long.gmt0(): Long {
    val calendar: Calendar = GregorianCalendar()
    val timeZone = calendar.timeZone
    return this - timeZone.rawOffset
}

fun Long.toDatetime(pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS"): String =
    SimpleDateFormat(pattern, Locale.ENGLISH).format(this)

fun String.toLongDate(pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS"): Long = try {
    SimpleDateFormat(pattern, Locale.getDefault()).parse(
        this.replace("Z$".toRegex(), "+0000")
    )?.time ?: 0
} catch (e: Exception) {
    0
}