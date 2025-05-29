package com.charchil.reminderpro.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun Form(
    time: String,
    onTimeClick: () -> Unit,
    onSubmit: (String, String, Boolean) -> Unit,
    initialName: String = "",
    initialDosage: String = "",
    initialRepeat: Boolean = false
) {
    var name by remember { mutableStateOf(initialName) }
    var dosage by remember { mutableStateOf(initialDosage) }
    var repeat by remember { mutableStateOf(initialRepeat) }

    val context = LocalContext.current // 👈 Context for Toast

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dosage,
            onValueChange = { dosage = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTimeClick() },
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Select Time",
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Time: $time")
            }
        }

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = repeat, onCheckedChange = { repeat = it })
            Text("Repeat")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (name.isBlank() || dosage.isBlank()) {
                    // ⚠️ Show warning if any field is empty
                    Toast.makeText(context, "Please fill all fields⚠\uFE0F", Toast.LENGTH_SHORT).show()
                } else {
                    // ✅ Save reminder
                    onSubmit(name, dosage, repeat)
                    name = ""
                    dosage = ""
                    repeat = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Reminder")
        }
    }
}
