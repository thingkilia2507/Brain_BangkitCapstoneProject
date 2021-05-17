package com.thing.bangkit.soulmood.helper

import java.text.SimpleDateFormat
import java.util.*


object DateHelper {
    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in", "ID"))
        val date = Date()
        return dateFormat.format(date)
    }
    fun convertDateToMonthYearFormat(date:String):String{
        val df = SimpleDateFormat("yyyy-MM", Locale("in", "ID"))
        val df1 = SimpleDateFormat("MMM yyyy", Locale("in", "ID"))
        val date1 = df.parse(date)
        return df1.format(date1)
    }
    fun dateFormat(tanggal: String): String {
        var newFormat = "dd MMMM yyyy"
        var format = SimpleDateFormat("yyyy-MM-dd", Locale("in", "ID"))
        var date: Date? = null
        date = format.parse(tanggal)
        format.applyPattern(newFormat)
        var dates = format.format(date)
        dates.replace("00:00:00 GMT+07:00", "")
        return dates
    }
}