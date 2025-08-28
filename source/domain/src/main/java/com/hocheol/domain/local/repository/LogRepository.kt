package com.hocheol.domain.local.repository

import java.io.File

interface LogRepository {
    fun writeLog(level: String, message: String)
    fun compressLogFiles(): File
}