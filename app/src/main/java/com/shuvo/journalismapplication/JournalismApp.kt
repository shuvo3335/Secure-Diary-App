package com.shuvo.journalismapplication

import android.app.Application
import com.shuvo.journalismapplication.data.AppDatabase

class JournalismApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
