package com.ybg.yxym.yueshow.view.gallery.utils

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {

    fun timeFormat(timeMillis: Long, pattern: String): String {
        val format = SimpleDateFormat(pattern, Locale.CHINA)
        return format.format(Date(timeMillis))
    }

    fun formatPhotoDate(time: Long): String {
        return timeFormat(time, "yyyy-MM-dd")
    }

    fun formatPhotoDate(path: String): String {
        val file = File(path)
        if (file.exists()) {
            val time = file.lastModified()
            return formatPhotoDate(time)
        }
        return "1970-01-01"
    }
}
