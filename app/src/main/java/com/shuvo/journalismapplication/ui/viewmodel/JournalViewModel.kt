package com.shuvo.journalismapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shuvo.journalismapplication.data.JournalEntry
import com.shuvo.journalismapplication.data.JournalEntryDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournalViewModel(private val journalEntryDao: JournalEntryDao) : ViewModel() {

    val allEntries: StateFlow<List<JournalEntry>> = journalEntryDao.getAllEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveEntry(title: String, content: String, mood: String, date: Long, id: Int = 0) {
        viewModelScope.launch {
            val entry = JournalEntry(
                id = id,
                title = title,
                content = content,
                mood = mood,
                date = date
            )
            journalEntryDao.insertEntry(entry)
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            journalEntryDao.deleteEntry(entry)
        }
    }
}

class JournalViewModelFactory(private val journalEntryDao: JournalEntryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalViewModel(journalEntryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
