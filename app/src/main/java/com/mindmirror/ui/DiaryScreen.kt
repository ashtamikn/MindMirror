package com.mindmirror.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindmirror.data.DiaryEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiaryScreen(viewModelFactory: ViewModelProvider.Factory) {
    val viewModel: DiaryViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "MindMirror",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Write your day. Review it later. Get quick guidance.")
        }

        item {
            OutlinedTextField(
                value = state.contentDraft,
                onValueChange = viewModel::updateContentDraft,
                label = { Text("What happened today?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.moodDraft,
                onValueChange = viewModel::updateMoodDraft,
                label = { Text("Mood (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = viewModel::saveEntry) {
                Text("Save entry")
            }
        }

        item {
            InsightsSection(insights = state.insights)
        }

        item {
            Text("Entries", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }

        items(state.entries, key = { it.id }) { entry ->
            EntryCard(entry = entry)
        }
    }
}

@Composable
private fun InsightsSection(insights: Insights) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Overall summary", fontWeight = FontWeight.SemiBold)
            Text(insights.summary)

            Text("What is going well", fontWeight = FontWeight.SemiBold)
            insights.whatWentWell.forEach {
                Text("- $it")
            }

            Text("What to improve", fontWeight = FontWeight.SemiBold)
            insights.improvements.forEach {
                Text("- $it")
            }
        }
    }
}

@Composable
private fun EntryCard(entry: DiaryEntry) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(entry.mood.ifBlank { "unspecified" }, fontWeight = FontWeight.SemiBold)
                Text(formatTime(entry.createdAtEpochMillis))
            }
            Text(entry.content)
        }
    }
}

private fun formatTime(epochMillis: Long): String {
    val format = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return format.format(Date(epochMillis))
}

