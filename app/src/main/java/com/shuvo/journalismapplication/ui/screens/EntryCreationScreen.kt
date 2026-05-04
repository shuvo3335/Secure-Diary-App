package com.shuvo.journalismapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shuvo.journalismapplication.ui.viewmodel.JournalViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.tooling.preview.Preview
import com.shuvo.journalismapplication.data.JournalEntry
import com.shuvo.journalismapplication.ui.theme.JournalismApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryCreationScreen(
    viewModel: JournalViewModel,
    entryId: Int? = null,
    onBackClick: () -> Unit
) {
    val entries by viewModel.allEntries.collectAsState()

    EntryCreationContent(
        entries = entries,
        entryId = entryId,
        onBackClick = onBackClick,
        onSaveEntry = { title, content, mood, date, id ->
            viewModel.saveEntry(title, content, mood, date, id)
            onBackClick()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryCreationContent(
    entries: List<JournalEntry>,
    entryId: Int? = null,
    onBackClick: () -> Unit,
    onSaveEntry: (String, String, String, Long, Int) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var selectedMood by rememberSaveable { mutableStateOf("Calm") }
    var selectedDate by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(entryId, entries) {
        if (entryId != null) {
            val entry = entries.find { it.id == entryId }
            entry?.let {
                title = it.title
                content = it.content
                selectedMood = it.mood
                selectedDate = it.date
            }
        }
    }

    val moods = listOf("Important", "Happy", "Sad", "Calm", "Inspired", "Anxious", "Grateful",
        "Excited", "Focused", "Energetic")
    var expanded by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
    var showDatePicker by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text(if (entryId == null) "New Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onSaveEntry(title, content, selectedMood, selectedDate, entryId ?: 0)
                            }
                        }
                    ) {
                        Text("Save", style = MaterialTheme.typography.titleMedium)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDate)),
                            onValueChange = { },
                            label = { Text("Date") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedMood,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Mood") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                moods.forEach { mood ->
                                    DropdownMenuItem(
                                        text = { Text(mood) },
                                        onClick = {
                                            selectedMood = mood
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Content") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp)
                        )

                        // Extra spacer to ensure we can scroll past the FAB or bottom of screen
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EntryCreationScreenPreview() {
    JournalismApplicationTheme {
        EntryCreationContent(
            entries = emptyList(),
            onBackClick = {},
            onSaveEntry = { _, _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EntryCreationScreenEditPreview() {
    JournalismApplicationTheme {
        EntryCreationContent(
            entries = listOf(
                JournalEntry(
                    id = 1,
                    title = "Sample Entry",
                    content = "This is a sample journal entry content.",
                    mood = "Happy",
                    date = System.currentTimeMillis()
                )
            ),
            entryId = 1,
            onBackClick = {},
            onSaveEntry = { _, _, _, _, _ -> }
        )
    }
}
