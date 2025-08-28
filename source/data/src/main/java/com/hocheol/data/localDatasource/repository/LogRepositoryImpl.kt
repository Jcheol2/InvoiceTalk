package com.hocheol.data.localDatasource.repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.hocheol.domain.local.repository.LogRepository
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val context: Context,
): LogRepository {

    override fun writeLog(level: String, message: String) {
        val fileName = "${context.packageName.split(".").last()}_log(${getCurrentDate()}).txt"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Android/media/${context.packageName}/Logs")
            }
        }

        val collection = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        var uri = context.contentResolver.query(collection, null, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val id = cursor.getLong(idColumn)
                ContentUris.withAppendedId(collection, id)
            } else {
                null
            }
        }

        if (uri == null) {
            uri = context.contentResolver.insert(collection, contentValues)
            deleteOldLogFiles()
        }

        uri?.let {
            try {
                context.contentResolver.openOutputStream(it, "wa")?.use { outputStream ->
                    val logEntry = "[${level.uppercase()}]${getCurrentTime()}: $message\n"
                    outputStream.write(logEntry.toByteArray())
                    when (level.lowercase()) {
                        "i" -> Log.i("LogRepository", logEntry)
                        "e" -> Log.e("LogRepository", logEntry)
                        else -> Log.d("LogRepository", logEntry)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteOldLogFiles() {
        val thirtyDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }.time
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).apply { isLenient = false }
        val collection = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ? AND ${MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf(
            "Android/media/${context.packageName}/Logs/%",
            "${context.packageName.split(".").last()}_log(%"
        )

        try {
            context.contentResolver.query(
                collection,
                arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME),
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))

                    val regex = Regex(".*_log\\((\\d{8})\\)\\.txt")
                    val match = regex.matchEntire(fileName)

                    if (match != null) {
                        val fileDateStr = match.groupValues[1]
                        val fileDate = try {
                            dateFormat.parse(fileDateStr)
                        } catch (e: Exception) {
                            null
                        }

                        if (fileDate != null && fileDate.before(thirtyDaysAgo)) {
                            val deleteUri = ContentUris.withAppendedId(collection, id)
                            context.contentResolver.delete(deleteUri, null, null)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
        return timeFormat.format(Date())
    }

    override fun compressLogFiles(): File {
        val zipFile = File(context.cacheDir, "logs_${getCurrentDate()}.zip")
        val collection = MediaStore.Files.getContentUri("external")
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
        val selectionArgs = arrayOf("Android/media/${context.packageName}/Logs/")

        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            context.contentResolver.query(
                collection,
                arrayOf(MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns._ID),
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val fileUri = ContentUris.withAppendedId(collection, id)

                    val zipEntry = ZipEntry(fileName)
                    zipOut.putNextEntry(zipEntry)
                    context.contentResolver.openInputStream(fileUri)?.use { input ->
                        input.copyTo(zipOut)
                    }
                    zipOut.closeEntry()
                }
            }
        }

        return zipFile
    }
}