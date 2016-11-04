package com.ybg.yxym.yueshow.view.gallery.utils

import android.content.Context
import android.os.Environment

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {

    fun createTmpFile(context: Context): File {

        val state = Environment.getExternalStorageState()
        if (state == Environment.MEDIA_MOUNTED) {
            // 已挂载
            val pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
            val fileName = "multi_image_" + timeStamp + ""
            val tmpFile = File(pic, fileName + ".jpg")
            return tmpFile
        } else {
            val cacheDir = context.cacheDir
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
            val fileName = "multi_image_" + timeStamp + ""
            val tmpFile = File(cacheDir, fileName + ".jpg")
            return tmpFile
        }

    }

}
