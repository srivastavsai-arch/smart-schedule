package com.smartschedule.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun normalizeDate(timeInMillis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun todayNormalized(): Long = normalizeDate(System.currentTimeMillis())

    fun formatDateLabel(date: Long): String {
        val today = todayNormalized()
        val cal = Calendar.getInstance()
        cal.timeInMillis = date
        return when (date) {
            today -> "Today"
            today + 86400000L -> "Tomorrow"
            else -> SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(date))
        }
    }

    fun formatShortDate(date: Long): String {
        return SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(date))
    }

    fun formatCompletionDate(date: Long?): String {
        return date?.let {
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it))
        } ?: ""
    }

    fun daysBetween(from: Long, to: Long): Int {
        return ((normalizeDate(to) - normalizeDate(from)) / 86400000L).toInt()
    }

    fun isToday(date: Long): Boolean = normalizeDate(date) == todayNormalized()

    fun isYesterday(date: Long): Boolean {
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
        return normalizeDate(date) == normalizeDate(yesterday)
    }

    fun getNextNDays(n: Int): List<Long> {
        val dates = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        for (i in 0 until n) {
            cal.timeInMillis = todayNormalized()
            cal.add(Calendar.DAY_OF_YEAR, i)
            dates.add(normalizeDate(cal.timeInMillis))
        }
        return dates
    }

    fun getThreeDaysAgoNormalized(): Long {
        return Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) }.let {
            normalizeDate(it.timeInMillis)
        }
    }
}
