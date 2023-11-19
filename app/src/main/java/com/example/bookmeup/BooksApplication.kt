package com.example.bookmeup

import android.app.Application
import com.example.bookmeup.data.AppContainer
import com.example.bookmeup.data.DefaultAppContainer

class BooksApplication : Application() {
    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
