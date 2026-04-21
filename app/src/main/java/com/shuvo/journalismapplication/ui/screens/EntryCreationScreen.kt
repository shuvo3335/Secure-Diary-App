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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryCreationScreen(
    viewModel: JournalViewModel,
    entryId: Int? = null,
    onBackClick: () -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var selectedMood by rememberSaveable { mutableStateOf("Calm") }
    var selectedDate by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }

    val entries by viewModel.allEntries.collectAsState()

    LaunchedEffect(entryId) {
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
                                viewModel.saveEntry(title, content, selectedMood, selectedDate, entryId ?: 0)
                                onBackClick()
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
//                item {
//                    // Header Image - Responsive height
//                    AsyncImage(
//                        model = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?q=80&w=1000",
//                        contentDescription = "Nature Header",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .heightIn(max = 100.dp)
//                            .aspectRatio(16f / 9f),
//                        contentScale = ContentScale.Crop
//                    )
//                }

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
