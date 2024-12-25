package com.example.sitaa.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {
    private const val DATE_FORMAT = "dd MMMM yyyy"
    private const val DATE_TIME_FORMAT = "dd MMMM yyyy, HH:mm"
    private const val TIME_FORMAT = "HH:mm"
    private val locale = Locale("id", "ID")

    fun formatDate(date: Date?): String {
        if (date == null) return "-"
        val formatter = SimpleDateFormat(DATE_FORMAT, locale)
        return formatter.format(date)
    }

    fun formatDateTime(date: Date?): String {
        if (date == null) return "-"
        val formatter = SimpleDateFormat(DATE_TIME_FORMAT, locale)
        return formatter.format(date)
    }

    fun formatTime(date: Date?): String {
        if (date == null) return "-"
        val formatter = SimpleDateFormat(TIME_FORMAT, locale)
        return formatter.format(date)
    }

    fun getRelativeTime(date: Date): String {
        val now = Date()
        val diff = now.time - date.time
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            seconds < 60 -> "Baru saja"
            minutes < 60 -> "$minutes menit yang lalu"
            hours < 24 -> "$hours jam yang lalu"
            days < 7 -> "$days hari yang lalu"
            else -> formatDate(date)
        }
    }

    fun getNotificationTimeGroup(date: Date): String {
        val now = Date()
        val diff = now.time - date.time
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            days < 1 -> "Hari ini"
            days < 7 -> "Minggu ini"
            days < 30 -> "Bulan ini"
            else -> "Sebelumnya"
        }
    }

    fun isToday(date: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.time = date
        val calendar2 = Calendar.getInstance()
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    fun parseDate(dateString: String): Date? {
        return try {
            val formatter = SimpleDateFormat(DATE_FORMAT, locale)
            formatter.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    fun parseDateTime(dateTimeString: String): Date? {
        return try {
            val formatter = SimpleDateFormat(DATE_TIME_FORMAT, locale)
            formatter.parse(dateTimeString)
        } catch (e: Exception) {
            null
        }
    }
}