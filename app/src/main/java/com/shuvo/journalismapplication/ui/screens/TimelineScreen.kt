package com.shuvo.journalismapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shuvo.journalismapplication.data.JournalEntry
import com.shuvo.journalismapplication.ui.viewmodel.JournalViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.shuvo.journalismapplication.ui.theme.JournalismApplicationTheme
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    viewModel: JournalViewModel,
    onAddEntryClick: () -> Unit,
    onEditEntryClick: (Int) -> Unit
) {
    val entries by viewModel.allEntries.collectAsState()
    TimelineScreenContent(
        entries = entries,
        onAddEntryClick = onAddEntryClick,
        onEditEntryClick = onEditEntryClick,
        onDeleteEntryClick = { viewModel.deleteEntry(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreenContent(
    entries: List<JournalEntry>,
    onAddEntryClick: () -> Unit,
    onEditEntryClick: (Int) -> Unit,
    onDeleteEntryClick: (JournalEntry) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diary Timeline") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEntryClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Show image only in Portrait mode
            if (!isLandscape) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?q=80&w=1000",
                    contentDescription = "Tranquil Forest",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (entries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No entries yet. Write New Task!")
                        }
                    }
                } else {
                    items(entries) { entry ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            JournalEntryItem(
                                entry = entry,
                                onEditClick = { onEditEntryClick(entry.id) },
                                onDeleteClick = { onDeleteEntryClick(entry) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JournalEntryItem(
    entry: JournalEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var hasVisualOverflow by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = entry.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = entry.mood,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEditClick()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.error,
                                leadingIconColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(entry.date)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult ->
                    hasVisualOverflow = textLayoutResult.hasVisualOverflow
                }
            )
            if (hasVisualOverflow || expanded) {
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.heightIn(min = 32.dp)
                ) {
                    Text(
                        text = if (expanded) "Show less" else "Show more",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimelineScreenPreview() {
    JournalismApplicationTheme {
        TimelineScreenContent(
            entries = listOf(
                JournalEntry(
                    id = 1,
                    title = "Morning Walk",
                    date = System.currentTimeMillis(),
                    content = "Saw a beautiful sunrise today at the park. The weather was perfect for a long walk.",
                    mood = "Happy"
                ),
                JournalEntry(
                    id = 2,
                    title = "Coding Session",
                    date = System.currentTimeMillis() - 86400000,
                    content = "Worked on the journal app. Learned about Compose Previews and how to extract stateless composables.",
                    mood = "Productive"
                ),
                JournalEntry(
                    id = 3,
                    title = "Rainy Day",
                    date = System.currentTimeMillis() - 172800000,
                    content = "It rained all day. Perfect for reading a book and sipping some hot cocoa.",
                    mood = "Cozy"
                )
            ),
            onAddEntryClick = {},
            onEditEntryClick = {},
            onDeleteEntryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimelineScreenEmptyPreview() {
    JournalismApplicationTheme {
        TimelineScreenContent(
            entries = emptyList(),
            onAddEntryClick = {},
            onEditEntryClick = {},
            onDeleteEntryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JournalEntryItemPreview() {
    JournalismApplicationTheme {
        JournalEntryItem(
            entry = JournalEntry(
                id = 1,
                title = "Sample Entry",
                date = System.currentTimeMillis(),
                content = "This is a sample journal entry content to show how it looks in the list. It can be quite long sometimes.",
                mood = "Happy"
            ),
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
