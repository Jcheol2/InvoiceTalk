package com.hocheol.invoicetalk

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.hocheol.presentation.MainActivity
import kotlin.system.exitProcess

class InvoiceTalkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i(this.javaClass.simpleName, "application onCreate")

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            val bundle = Bundle().apply {
                putString("exceptionMessage", e.localizedMessage)
                putString("stackTrace", Log.getStackTraceString(e))
            }

            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra("uncaughtException", bundle)
            })

            exitProcess(0)
        }
    }
}