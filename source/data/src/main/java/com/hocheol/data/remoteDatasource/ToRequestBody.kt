package com.hocheol.data.remoteDatasource

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun Any.toJsonRequestBody(): RequestBody {
    val gson = Gson()
    val json = gson.toJson(this)
    val mediaType = "application/json".toMediaTypeOrNull()
    return json.toRequestBody(mediaType)
}

fun File.toZipRequestBody(): RequestBody {
    val mediaType = "application/zip".toMediaTypeOrNull()
    return asRequestBody(mediaType)
}